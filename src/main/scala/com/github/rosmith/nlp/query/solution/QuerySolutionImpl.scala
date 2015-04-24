package com.github.rosmith.nlp.query.solution

import scala.collection.mutable.Map

class QuerySolutionImpl(m: Map[String, List[Any]], cM: Map[String, Class[_]], vars: List[String]) extends IQuerySolution {

  private var empty = false

  private var variables = vars

  private var map = m

  private var classMap = cM

  private var _errorMessage = ""

  def this() {
    this(null, null, null)
    empty = true
  }

  def this(eM: String) {
    this(null, null, null)
    empty = true
    _errorMessage = eM
  }

  def getType(varName: String): Class[_] = {
    if (classMap.get(varName).exists { x => x != null })
      classMap get (varName) get
    else
      null
  }

  def getValues(varName: String): List[Any] = {
    if (map.get(varName).exists { x => x != null })
      map get (varName) get
    else
      null
  }

  def getVariables(): List[String] = {
    variables
  }

  def isEmpty() = empty

  def errorMessage() = _errorMessage

  def print() {
    println("####### RESULTS ##########")
    if (isEmpty) {
      println(null)
    } else {
      println(getVariables.mkString(" => "))
      var size = getValues(getVariables.apply(0)).size
      for (i <- 0 to size - 1) {
        var varSize = getVariables.size
        var arr: Array[String] = Array[String]()
        for (j <- 0 to varSize - 1) {
          var v = getVariables()(j)
          arr = arr :+ getValues(v).apply(i).toString
        }
        println(arr.mkString("[", " => ", "]"))
      }
    }
  }

}
