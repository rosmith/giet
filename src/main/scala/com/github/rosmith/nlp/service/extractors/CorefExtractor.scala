package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.service.store.Triple

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "coref", multirelation = true)
class CorefExtractor extends AbstractExtractor {

  private val relations = List("corefValue")

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()

    doc.corefs.foreach(t => {
      if (t.references != null) {
        list = list :+ new Triple(true).subj_(t.identity).pred_("coref").obj_(t.references.identity)
        list = list :+ new Triple().subj_(t.identity).pred_(Array("coref", "corefValue").mkString("_")).obj_(t.value)
        list = list :+ new Triple().subj_(t.references.identity).pred_(Array("coref", "corefValue").mkString("_")).obj_(t.references.value)
      }
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.COREF).asJava
  }

}