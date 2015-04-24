package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.service.processor.StanfordProcessor
import com.github.rosmith.nlp.service.store.Triple

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace="is")
class NamedEntityExtractor extends AbstractExtractor {

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.words.foreach(w => {
      if(!"O".equals(w.ner)) {
        list = list :+ new Triple().subj_(w.identity).pred_("is").obj_(w.ner)
      }
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    false
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.NE).asJava
  }

}