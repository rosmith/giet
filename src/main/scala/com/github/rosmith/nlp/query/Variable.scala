package com.github.rosmith.nlp.query

class Variable(_val: String) extends Serializable {
  
  assert(_val != null && !_val.isEmpty)

  private var _value: String = _val
  
  private var _inferedType: String = null
  
  def value = _value
  
  def inferedType(iT: String) {
    _inferedType = iT
  }
  
  def inferedType = _inferedType
  
  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[Variable]) {
      var variable = obj.asInstanceOf[Variable]
      variable.value.equals(value)
    } else false
  }
  
}