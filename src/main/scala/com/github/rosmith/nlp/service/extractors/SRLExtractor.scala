package com.github.rosmith.nlp.service.extractors

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.service.store.Triple
import scala.collection.mutable.Map
import com.github.rosmith.nlp.query.model.SemanticRoleArgumentModel

import java.{util => ju}
import scala.collection.JavaConverters._

@Plugin(namespace = "srl", multirelation = true)
class SRLExtractor extends AbstractExtractor {

  private var relations = List("relation", "subject", "object")

  private val modifierMap = Map[String, String]()

  modifierMap.put("COM", "comitative")
  modifierMap.put("LOC", "locative")
  modifierMap.put("DIR", "directional")
  modifierMap.put("GOL", "goal")
  modifierMap.put("MNR", "manner")
  modifierMap.put("EXT", "extent")
  modifierMap.put("REC", "reciprocal")
  modifierMap.put("PRD", "predication")
  modifierMap.put("PRP", "purpose")
  modifierMap.put("CAU", "cause")
  modifierMap.put("DIS", "discourse")
  modifierMap.put("ADV", "adjectival")
  modifierMap.put("MOD", "modal")
  modifierMap.put("NEG", "negation")
  modifierMap.put("DSP", "directspeech")
  modifierMap.put("SLC", "relative")
  modifierMap.put("LVB", "lightverb")

  modifierMap.keys.foreach(key => {
    relations = relations :+ modifierMap.apply(key)
  })

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.srl.foreach(srl => {
      srl.elements.foreach(elem => {
        list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", "relation").mkString("_")).obj_(elem.predicate.predicateValue)

        var tmp = elem.arguments.filter(arg => arg.argType.contains("A0"))

        var arg0: SemanticRoleArgumentModel = if (tmp.size > 0) tmp.apply(0) else null

        tmp = elem.arguments.filter(arg => arg.argType.contains("A1"))

        var arg1: SemanticRoleArgumentModel = if (tmp.size > 0) tmp.apply(0) else null

        tmp = elem.arguments.filter(arg => arg.argType.contains("A2"))

        var arg2: SemanticRoleArgumentModel = if (tmp.size > 0) tmp.apply(0) else null

        if (arg0 != null) {
          list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", "subject").mkString("_")).obj_(arg0.argValue)
          if (arg1 != null) {
            list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", "object").mkString("_")).obj_(arg1.argValue)
          }
        } else if (arg1 != null) {
          list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", "subject").mkString("_")).obj_(arg1.argValue)
          if (arg1 != null) {
            list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", "object").mkString("_")).obj_(arg2.argValue)
          }
        }

        elem.arguments.filter(arg => {
          var value = arg.argType
          !value.contains("A0") && !value.contains("A1") && !value.contains("A2")
        }).foreach(arg => {
          val argType = arg.argType.split("-").apply(1)

          list = list :+ new Triple().subj_(elem.identity).pred_(Array("srl", modifierMap.apply(argType)).mkString("_")).obj_(arg.argValue)
        })
      })
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.SRL).asJava
  }

}