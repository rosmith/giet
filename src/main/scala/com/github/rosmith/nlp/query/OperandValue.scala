package com.github.rosmith.nlp.query

class OperandValue(_val: Any, iv: Boolean) extends Serializable {
  
  assert(_val != null)

  private var _value: Any = _val
  
  private var _isVar: Boolean = iv
  
  def value = _value
  
  def isVar = _isVar
  
  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[OperandValue]) {
      var other = obj.asInstanceOf[OperandValue]
      other.value.equals(value) && isVar == other.isVar
    } else false
  }
  
  override def toString() = {
    value.toString()
  }
  
}