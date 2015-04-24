package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import net.sf.extjwnl.dictionary.Dictionary
import com.github.rosmith.nlp.service.store.Triple
import net.sf.extjwnl.data.list.PointerTargetNodeList
import net.sf.extjwnl.data.PointerUtils
import net.sf.extjwnl.data.POS
import net.sf.extjwnl.data.IndexWord
import java.util.function.Consumer
import scala.collection.mutable.Map
import java.util.concurrent.atomic.AtomicInteger
import net.sf.extjwnl.data.Synset

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "wordnet", multirelation = true)
class WordNetExtractor extends AbstractExtractor {

  private val relations = List(
    "body", "change", "cognition",
    "communication", "competition",
    "consumption", "contact", "creation",
    "emotion", "motion", "perception", "actor",
    "possession", "social", "stative", "weather")

  private val map = Map[String, AtomicInteger]()

  relations.foreach(r => map.put(r, new AtomicInteger))

  private var dictionary = Dictionary.getDefaultResourceInstance()

  private val VERB = "VB"

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()

    val rel = Array("nsubj", "nsubjpass", "xsubj")

    var tuples = doc.typedDependencies.filter(td => rel.contains(td.value)).map(td => {
      var gov = doc.words.filter(w => w.identity.equals(td.governorIdentity)).apply(0)
      var dep = doc.words.filter(w => w.identity.equals(td.dependentIdentity)).apply(0)
      doc.typedDependencies.filter(td => td.value.equals("nn") && td.governorIdentity.equals(dep.identity)).foreach(d => {
        var other = doc.words.filter(w => w.identity.equals(d.dependentIdentity)).apply(0)
        var value = Array(other.word, dep.word).mkString(" ")
        dep.word(value)
      })
      (gov, dep)
    })

    doc.words.filter(w => {
      w.tag.startsWith(VERB)
    }).foreach(w => {
      var iword = dictionary.getIndexWord(POS.VERB, w.lemma)
      var senses = iword.getSenses
      senses.toArray(new Array[Synset](senses.size)).foreach(synset => {
        var lexname = synset.getLexFileName
        lexname = lexname.substring(lexname.lastIndexOf(".") + 1)
        var identity = Array(lexname.toUpperCase(), map.apply(lexname).incrementAndGet()).mkString("_")

        var pair = tuples.filter(x => x._1.word.equals(w.word))

        if (pair.size > 0) {
          list = list :+ new Triple(true).subj_(w.identity).pred_(Array("wordnet", lexname).mkString("_")).obj_(identity)
//          list = list :+ new Triple().subj_(identity).pred_(Array("wordnet", "event").mkString("_")).obj_(pair.apply(0)._1.identity)

          // Coreference resolution
          var reference = doc.corefs.filter(c => c.value.equals(pair.apply(0)._2.word) && c.references != null)

          if (reference.size > 0) {
            list = list :+ new Triple().subj_(identity).pred_(Array("wordnet", "actor").mkString("_")).obj_(reference.apply(0).references.value)
          } else {
            list = list :+ new Triple(true).subj_(identity).pred_(Array("wordnet", "actor").mkString("_")).obj_(pair.apply(0)._2.word)
          }

        }
      })
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.WORD, Requirement.LEMMA, Requirement.TAG, Requirement.DEPENDENCY, Requirement.COREF).asJava
  }

}