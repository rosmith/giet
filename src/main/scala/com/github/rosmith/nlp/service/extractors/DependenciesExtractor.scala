package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.service.store.Triple
import com.github.rosmith.nlp.service.srl.StopWord
import edu.stanford.nlp.trees.TypedDependency
import com.github.rosmith.nlp.query.model.AnnotatedSentence

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "dependency", multirelation = true)
class DependenciesExtractor extends AbstractExtractor {

  private val relations = List(
    "dep", "aux", "auxpass", "cop",
    "conj", "cc", "arg", "subj", "agent",
    "nsubj", "nsubjpass", "csubj",
    "comp", "obj", "dobj", "iobj",
    "pobj", "attr", "ccomp", "xcomp",
    "compl", "mark", "rel", "acomp",
    "ref", "expl", "mod", "dependencyValue",
    "advcl", "purpcl", "tmod", "rcmod",
    "amod", "infmod", "partmod", "num",
    "number", "appos", "nn", "abbrev",
    "advmod", "neg", "poss", "possessive",
    "prt", "det", "prep", "sdep", "xsubj")

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.typedDependencies.foreach(t => {
      list = list :+ new Triple(true).subj_(t.dependentIdentity).pred_("dependency").obj_(t.identity)
      list = list :+ new Triple().subj_(t.identity).pred_(Array("dependency", "dependencyValue").mkString("_")).obj_(t.value)
      list = list :+ new Triple(true).subj_(t.governorIdentity).pred_(Array("dependency", t.value).mkString("_")).obj_(t.dependentIdentity)
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.DEPENDENCY).asJava
  }

}