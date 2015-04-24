package com.github.rosmith.nlp.query.filter

import com.github.rosmith.nlp.query.filter.BinaryOperator._
import com.github.rosmith.nlp.query.filter.Datatype._
import com.github.rosmith.nlp.query.OperandValue

class DatatypeQueryFilter(_var: OperandValue, dT: String) extends BinaryQueryFilter(_var, EQUALS, new OperandValue(dT, false)) {
  
  private var _dT = dT
  
  def this(_var: OperandValue) {
    this(_var, null)
  }
  
  def datatype(dataT: Datatype) = {
    _dT = Datatype.toString(dataT)
  }

  def datatype = _dT
  
}