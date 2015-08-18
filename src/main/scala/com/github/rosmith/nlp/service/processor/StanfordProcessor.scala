package com.github.rosmith.nlp.service.processor

import java.util.Properties
import scala.collection.mutable.Map
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.query.model.CorefModel
import com.github.rosmith.nlp.query.model.TypedDependencyModel
import com.github.rosmith.nlp.query.model.WordModel
import com.github.rosmith.nlp.service.srl.Blacklist
import org.slf4j.LoggerFactory
import edu.stanford.nlp.dcoref.CorefChain.CorefMention
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.trees.TypedDependency
import edu.stanford.nlp.util.CoreMap
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation
import com.github.rosmith.nlp.service.annotator.SemanticRoleAnnotator.SemanticRoleAnnotation
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation

class StanfordProcessor extends Processor {

  private var _pipeline: StanfordCoreNLP = null

  def init(): Boolean = {
    try {
      val props = new Properties
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref"/*, srl"*/)
//      props.setProperty("customAnnotatorClass.srl", "com.github.rosmith.nlp.service.annotator.SemanticRoleAnnotator")

      System.err.println("Creating pipeline...")
      _pipeline = new StanfordCoreNLP(props)
      true
    } catch {
      case e: Exception => {
        e.printStackTrace()
        false
      }
    }
  }

  def process(sentence: String): AnnotatedSentence = {
    val annotation = new Annotation(sentence)

    System.err.println("Annotating sentence...")
    _pipeline.annotate(annotation)

    val coreMaps: java.util.List[CoreMap] = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])

    var modelWords = Map[Int, List[WordModel]]()
    var modelTypedDeps = Map[Int, List[TypedDependencyModel]]()

    var size = coreMaps.size()

    for (j <- 0 to size - 1) {

      try {
        var coreMap = coreMaps.get(j)

        val coreLabels = coreMap.get(classOf[TokensAnnotation])

        var words = List[WordModel]()
        var typedDeps = List[TypedDependencyModel]()

        for (i <- 0 to coreLabels.size - 1) {
          if (!Blacklist.contains(coreLabels.get(i).word)) {
            var wordModel = new WordModel
            wordModel word coreLabels.get(i).word
            wordModel lemma coreLabels.get(i).lemma
            wordModel tag coreLabels.get(i).tag
            wordModel ner coreLabels.get(i).ner
            wordModel position coreLabels.get(i).index
            wordModel sentencePosition coreLabels.get(i).sentIndex

            words = words :+ wordModel
          }
        }

        val typedDependencies = coreMap.get(classOf[CollapsedCCProcessedDependenciesAnnotation]).typedDependencies

        typedDependencies.toArray(new Array[TypedDependency](typedDependencies.size())).foreach(dependency => {
          var depWord = dependency.dep().word()
          var govWord = dependency.gov().word()
          if (govWord != null && !Blacklist.contains(govWord) && !Blacklist.contains(depWord)) {
            var dep = dependency.dep
            var gov = dependency.gov
            var reln = dependency.reln

            var dependentWord = words.filter(w => w.position == dep.index).apply(0)
            var governorWord = words.filter(w => w.position == gov.index).apply(0)

            var typedDep = new TypedDependencyModel(dependentWord, governorWord)
            typedDep sentencePosition dependentWord.sentencePosition
            typedDep value reln.getShortName

            typedDeps = typedDeps :+ typedDep
          }
        })

        modelWords.put(j, words)
        modelTypedDeps.put(j, typedDeps)
      } catch {
        case e: Exception => getLogger.error(e.getMessage, e)
      }
    }

    var words = Array[WordModel]()
    var typedDeps = Array[TypedDependencyModel]()

    modelWords.foreach{
      case (index, list) => {
        list.foreach(l => {
          if(!words.contains(l)) {
            words = words :+ l
          }
        })
      }
    }

    modelTypedDeps.foreach{
      case (index, list) => {
        list.foreach(l => {
          if(!typedDeps.contains(l)) {
            typedDeps = typedDeps :+ l
          }
        })
      }
    }

    var srl = annotation.get(classOf[SemanticRoleAnnotation])

    var coreferences = extractCoreferences(annotation)
    var annotatedSentence = new AnnotatedSentence(sentence)
    annotatedSentence.words(words)
    annotatedSentence.typedDependencies(typedDeps)
    annotatedSentence.corefs(coreferences)
    annotatedSentence.srl(srl)

//    srl.foreach(s => {
//      s.elements.foreach(e => {
//        println(e.toString)
//      })
//    })

    annotatedSentence
  }

  private def extractCoreferences(annotation: Annotation) = {
    var map = annotation.get(classOf[CorefChainAnnotation])
    var coreferences = Array[CorefModel]()
    map.keySet().toArray(new Array[Integer](map.size())).foreach(key => {
      var chain = map get key
      var representativeMention = chain getRepresentativeMention;
      var refered = createCorefFromMention(representativeMention)
      if (!coreferences.contains(refered)) {
        coreferences = coreferences :+ refered
      }
      var mentions = chain getMentionsInTextualOrder;
      mentions.toArray(new Array[CorefMention](mentions.size())).foreach(mention => {
        if (mention != representativeMention) {
          var referee = createCorefFromMention(mention)
          if (!coreferences.contains(referee)) {
            referee references refered
            coreferences = coreferences :+ referee
          }
        }
      })
    })
    coreferences
  }

  private def createCorefFromMention(mention: CorefMention) = {
    var coref = new CorefModel
    coref.value(mention.mentionSpan)
    coref.position(mention.startIndex)
    coref.sentencePosition(mention.sentNum)
    coref
  }

  def getLogger = LoggerFactory.getLogger(classOf[StanfordProcessor])

}