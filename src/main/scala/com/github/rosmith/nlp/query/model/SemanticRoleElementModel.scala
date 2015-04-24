package com.github.rosmith.nlp.query.model

class SemanticRoleElementModel extends Serializable {

  private var elemSentPos: Int = -1

  private var pred: SemanticRolePredicateModel = null

  private var _arguments: List[SemanticRoleArgumentModel] = List()

  def predicate(p: SemanticRolePredicateModel) = {
    pred = p
    pred.sentPos(elemSentPos)
  }

  def predicate = pred

  def addArgument(v: String, t: String) {
    var arg = new SemanticRoleArgumentModel(this)
    arg.argValue(v)
    arg.argType(t)
    arg.sentPos(elemSentPos)
    if(!_arguments.contains(arg)){
      _arguments = _arguments :+ arg
    }
  }

  def sentPos(p: Int) = elemSentPos = p

  def arguments = _arguments

  def sentPos = elemSentPos

  def identity: String = {
    Array("Element", elemSentPos, predicate.predicateSense.split("\\.").apply(1), arguments.size).mkString("_")
  }

  override def toString = {
    var result = new StringBuffer
    result.
      append(identity).
      append(": \n").
      append("\t => Sentence position = ").
      append(sentPos).
      append("\n").
      append("\t => ").
      append(predicate.toString)

      arguments.foreach(a => {
        result.
          append("\n").
          append("\t => ").
          append(a.toString)
      })

      result.toString

  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[SemanticRoleElementModel]) {
      var elem = obj.asInstanceOf[SemanticRoleElementModel]
      sentPos == elem.sentPos &&
      predicate.equals(elem.predicate) &&
      arguments.size == elem.arguments.size &&
      arguments.filter(e => !elem.arguments.contains(e)) == 0 &&
      elem.arguments.filter(e => !arguments.contains(e)) == 0
    } else {
      false
    }
  }

}