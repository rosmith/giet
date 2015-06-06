package com.github.rosmith.nlp.query.model

class TypedDependencyModel(dependent: WordModel, governor: WordModel) extends Serializable {

  private var _sentencePosition: Int = -1

  private var _value: String = null

  dependent.typedDep(this)

  def sentencePosition(sP: Int) = _sentencePosition = sP

  def value(s: String) = _value = s

  def dependentIdentity = dependent.identity

  def governorIdentity = governor.identity

  def sentencePosition = _sentencePosition

  def value = _value

  def identity() = {
    Array("TypedDep", dependent.position, governor.position, sentencePosition).mkString("_")
  }

  def ===(tdm: TypedDependencyModel) = {
    this.equals(tdm)
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[TypedDependencyModel]) {
      var tdm = obj.asInstanceOf[TypedDependencyModel]
      dependentIdentity === tdm.dependentIdentity &&
        governorIdentity === tdm.governorIdentity &&
        sentencePosition == tdm.sentencePosition
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