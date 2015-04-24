package com.github.rosmith.nlp.query.model

class SemanticRoleArgumentModel(parent: SemanticRoleElementModel) extends Serializable {

  private var _type: String = null

  private var value: String = null

  private var _sentPos: Int = -1

  def argType(t: String) = _type = t

  def argValue(v: String) = value = v

  def argType = _type

  def argValue = value

  def sentPos(p: Int) = _sentPos = p

  def sentPos = _sentPos

  def parentIdentity = Array(parent.identity, argType.split("-").apply(0)).mkString("_")

  def identity = {
    Array("Argument", _sentPos, argType.split("-").apply(0)).mkString("_")
  }

  override def toString = {
    Array(argValue, argType).mkString("{", ", ", "}")
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[SemanticRoleArgumentModel]) {
      var arg = obj.asInstanceOf[SemanticRoleArgumentModel]
      argType === arg.argType &&
      argValue === arg.argValue &&
      sentPos == arg.sentPos
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