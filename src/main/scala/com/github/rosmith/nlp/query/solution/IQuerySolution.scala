package com.github.rosmith.nlp.query.solution

trait IQuerySolution extends Serializable {

  def getType(varName: String): Class[_]

  def getValues(varName: String): List[Any]

  def getVariables: List[String]

  def isEmpty(): Boolean

  def errorMessage(): String

  def print()

}
