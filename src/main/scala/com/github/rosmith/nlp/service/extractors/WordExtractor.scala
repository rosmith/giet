package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.service.store.Triple
import com.github.rosmith.nlp.query.model.AnnotatedSentence

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "word")
class WordExtractor extends AbstractExtractor {

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.words.foreach(w => {
      list = list :+ new Triple().subj_(w.identity).pred_("word").obj_(w.word)
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    false
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.WORD).asJava
  }

}