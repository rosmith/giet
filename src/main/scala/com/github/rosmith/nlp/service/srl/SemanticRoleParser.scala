package com.github.rosmith.nlp.service.srl

import scala.collection.mutable.LinkedHashMap
import com.github.rosmith.nlp.query.model.SemanticRoleFrameModel
import com.github.rosmith.nlp.query.model.SemanticRoleElementModel
import com.github.rosmith.nlp.query.model.SemanticRolePredicateModel
import com.github.rosmith.nlp.query.model.SemanticRoleArgumentModel
import org.slf4j.LoggerFactory

class SemanticRoleParser {

}

object SemanticRoleParser {

  private val LOG = LoggerFactory.getLogger(classOf[SemanticRoleParser])

  private var predicateMap: LinkedHashMap[Int, (String, String)] = null

  private var argumentsMap: LinkedHashMap[Int, List[(String, String)]] = null

  private var frames: Array[SemanticRoleFrameModel] = Array()

  def parse(input: Array[Array[String]]) = {
    frames = Array()
    input.foreach(i => process(i))
    frames
  }

  private def process(input: Array[String]) {
    predicateMap = LinkedHashMap[Int, (String, String)]()

    argumentsMap = LinkedHashMap[Int, List[(String, String)]]()

    LOG.info("Processing srl predicates...")
    input.foreach(i => processPredicate(i))

    LOG.info("Processing srl elements...")
    input.foreach(i => processElements(i))

    if (predicateMap.size > 0) {
      var frame = new SemanticRoleFrameModel
      frame.position(frames.size)

      predicateMap.keys.foreach(index => {
        var elem = new SemanticRoleElementModel
        frame.addElement(elem)

        var predTuple = predicateMap.apply(index)
        var predicate = new SemanticRolePredicateModel
        predicate.predicateValue(predTuple._1)
        predicate.predicateSense(predTuple._2)
        elem.predicate(predicate)

        var arguments = argumentsMap.applyOrElse(index, (_: Int) => { List() })
        arguments.foreach(argTuple => {
          elem.addArgument(argTuple._1, argTuple._2)
        })

      })

      frames = frames :+ frame
    }

  }

  private def processPredicate(input: String) {
    println(input)
    var tmp = input.replaceAll("\\s+", " ")

    var entries = tmp.split(" ")

    var predEntry = entries.apply(4)

    if (predEntry != null && !predEntry.equals("_")) {
      var sense = predEntry.
        replace("|", " ").
        split(" ").
        filter(e => e.startsWith("pb=")).
        applyOrElse(0, (_: Int) => {
          ""
        }).
        split("=").
        applyOrElse(1, (_: Int) => { null })

      if (sense != null) {
        predicateMap.put(entries.apply(0).toInt, (entries.apply(1), sense))
      }
    }

  }

  private def processElements(input: String) {
    var tmp = input.replaceAll("\\s+", " ")
    var entries = tmp.split(" ")

    var argTypes = entries.apply(7)

    if (argTypes != null && !argTypes.equals("_")) {
      argTypes.
        split(";").
        foreach(arg => {
          var tmp = arg.split(":")
          var predIndex = tmp.apply(0).toInt
          var argType = tmp.apply(1)
          var list = argumentsMap.applyOrElse(predIndex, (_: Int) => { List() })
          list = list :+ (entries.apply(1), argType)
          argumentsMap.put(predIndex, list)
        })
    }

  }

}