package com.github.rosmith.nlp.query.model

class SemanticRolePredicateModel extends Serializable {

  private var _sense: String = null

  private var value: String = null

  private var _sentPos: Int = -1

  def predicateSense(t: String) = _sense = t

  def predicateValue(v: String) = value = v

  def predicateSense = _sense

  def predicateValue = value

  def sentPos(p: Int) = _sentPos = p

  def sentPos = _sentPos

  def identity = {
    Array("Predicate", _sentPos, predicateSense.split("\\.").apply(1)).mkString("_")
  }

  override def toString = {
    Array(predicateValue, predicateSense).mkString("{", ", ", "}")
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[SemanticRolePredicateModel]) {
      var pred = obj.asInstanceOf[SemanticRolePredicateModel]
      predicateSense === pred.predicateSense &&
      predicateValue === pred.predicateValue &&
      sentPos == pred.sentPos
    } else {
      false
    }
  }

  protected implicit class StringImplicit(str: String) {
    def ===(that: String): Boolean = {
      if (str == null || that == null) {
        false
      } else {
        str.equals(that)
      }
    }
  }

}