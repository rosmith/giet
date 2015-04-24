package com.github.rosmith.nlp.query.filter

import com.github.rosmith.nlp.query.filter.Datatype._
import com.github.rosmith.nlp.query.filter.BinaryOperator._
import com.github.rosmith.nlp.query.OperandValue

class CountQueryFilter(_var: OperandValue, op: BinaryOperator, _val: Int) extends BinaryQueryFilter(_var, op, new OperandValue(_val, false)) {

}