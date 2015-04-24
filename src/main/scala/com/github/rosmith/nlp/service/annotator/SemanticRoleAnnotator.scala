package com.github.rosmith.nlp.service.annotator

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Collections
import java.util.Properties
import java.util.Set
import java.util.zip.ZipInputStream
import com.github.rosmith.nlp.query.model.SemanticRoleFrameModel
import com.github.rosmith.nlp.service.srl.SemanticRoleParser
import com.github.rosmith.nlp.service.util.ServiceUtil
import com.github.rosmith.nlp.service.annotator.SemanticRoleAnnotator._
import com.googlecode.clearnlp.dependency.DEPTree
import com.googlecode.clearnlp.engine.EngineGetter
import com.googlecode.clearnlp.nlp.NLPDecode
import com.googlecode.clearnlp.nlp.NLPLib
import com.googlecode.clearnlp.reader.AbstractReader
import com.googlecode.clearnlp.util.UTInput
import edu.stanford.nlp.ling.CoreAnnotation
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.Annotator
import edu.stanford.nlp.pipeline.Annotator.Requirement
import edu.stanford.nlp.pipeline.Annotator.TOKENIZE_SSPLIT_PARSE
import edu.stanford.nlp.util.ErasureUtils
import org.slf4j.LoggerFactory

class SemanticRoleAnnotator(name: String, props: Properties) extends Annotator {

  private val LOG = LoggerFactory.getLogger(classOf[SemanticRoleAnnotator])

  private val ANNOTATOR_CLASS = "srl"

  private val CLEARNLP_SRL = ANNOTATOR_CLASS

  private val SEMANTIC_ROLE_REQUIREMENT = new Requirement(CLEARNLP_SRL)

  private val LANG = AbstractReader.LANG_EN

  private val dictFile = props.getProperty(Array(name, "dictFile").mkString("."), ServiceUtil.dict)

  private val posModelFile = props.getProperty(Array(name, "posModelFile").mkString("."), ServiceUtil.pos)

  private val depModelFile = props.getProperty(Array(name, "depModelFile").mkString("."), ServiceUtil.dep)

  private val predModelFile = props.getProperty(Array(name, "predModelFile").mkString("."), ServiceUtil.pred)

  private val roleModelFile = props.getProperty(Array(name, "roleModelFile").mkString("."), ServiceUtil.role)

  private val srlModelFile = props.getProperty(Array(name, "srlModelFile").mkString("."), ServiceUtil.srl)

  private val TOKENIZER = EngineGetter.getTokenizer(LANG, new FileInputStream(dictFile))

  private val TAGGER = EngineGetter.getComponent(new FileInputStream(posModelFile), LANG, NLPLib.MODE_POS)

  private val ANALYSER = EngineGetter.getComponent(new FileInputStream(dictFile), LANG, NLPLib.MODE_MORPH)

  private val PARSER = EngineGetter.getDEPParser(new ZipInputStream(new FileInputStream(depModelFile)), LANG)

  private val IDENTIFIER = EngineGetter.getComponent(new FileInputStream(predModelFile), LANG, NLPLib.MODE_PRED)

  private val CLASSIFIER = EngineGetter.getComponent(new FileInputStream(roleModelFile), LANG, NLPLib.MODE_ROLE)

  private val LABELER = EngineGetter.getComponent(new FileInputStream(srlModelFile), LANG, NLPLib.MODE_SRL)

  def annotate(document: Annotation): Unit = {

    System.err.println("srl annotation...")

    var sentence = document.get(classOf[CoreAnnotations.TextAnnotation])

    var baos = new ByteArrayOutputStream
    baos.write(sentence.getBytes("UTF-8"))

    var bais = new ByteArrayInputStream(baos.toByteArray())

    var segmenter = EngineGetter.getSegmenter(LANG, TOKENIZER)

    var listOfTokens = segmenter.getSentences(UTInput.createBufferedReader(bais))

    var srlParserInput = Array[Array[String]]()

    for (i <- 0 to listOfTokens.size - 1) {
      var tree = NLPDecode.toDEPTree(listOfTokens.get(i))
      var bestParse = parseBest(tree)

      tree = bestParse.o.asInstanceOf[DEPTree]
      var input = tree.toStringSRL().split("\n")

      srlParserInput = srlParserInput :+ input
    }

    var result = SemanticRoleParser.parse(srlParserInput)

    document.set(classOf[SemanticRoleAnnotation], result)

  }

  def requirementsSatisfied(): Set[Requirement] = {
    Collections.singleton(SEMANTIC_ROLE_REQUIREMENT)
  }

  def requires(): Set[Requirement] = {
    TOKENIZE_SSPLIT_PARSE
  }

  private def parseBest(tree: DEPTree) = {
    TAGGER.process(tree)
    ANALYSER.process(tree)

    PARSER.process(tree)
    var treesWithScores = PARSER.getParsedTrees(true) // Sorted according to the scores in descending orders.

    for (i <- 0 to treesWithScores.size - 1) {
      var pair = treesWithScores.get(i)
      var tmpTree = pair.o.asInstanceOf[DEPTree]

      IDENTIFIER.process(tmpTree)
      CLASSIFIER.process(tmpTree)

      LABELER.process(tmpTree)
    }

    treesWithScores.get(0)
  }

}

object SemanticRoleAnnotator {

  class SemanticRoleAnnotation extends CoreAnnotation[Array[SemanticRoleFrameModel]] {
    def getType = {
      ErasureUtils.uncheckedCast(classOf[Array[SemanticRoleFrameModel]])
    }
  }

}