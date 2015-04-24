package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.service.store.Triple
import com.github.rosmith.nlp.query.model.AnnotatedSentence

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "position", multirelation=true)
class PositionExtractor extends AbstractExtractor {
  
  private val relations = List("sentencePosition")

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.words.foreach(w => {
      list = list :+ new Triple().subj_(w.identity).pred_("position").obj_(String.valueOf(w.position))
      list = list :+ new Triple().subj_(w.identity).pred_(Array("position", "sentencePosition").mkString("_")).obj_(String.valueOf(w.sentencePosition))
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.POSITION).asJava
  }

}