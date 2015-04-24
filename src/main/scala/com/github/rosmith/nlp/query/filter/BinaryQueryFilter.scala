package com.github.rosmith.nlp.query.filter

import com.github.rosmith.nlp.query.filter.BinaryOperator._
import com.github.rosmith.nlp.query.Variable
import com.github.rosmith.nlp.query.OperandValue

class BinaryQueryFilter(lv: OperandValue, op: BinaryOperator, rv: OperandValue) extends SingleQueryFilter {

  private var _operator: BinaryOperator = op

  def this(lv: OperandValue, stringOp: String, rv: OperandValue) {
    this(lv, BinaryQueryFilter.toOperator(stringOp), rv)
  }

  def leftVariable = lv.value

  def rightVariable = rv.value

  def operator = _operator

  def rightValueIsVariable = rv.isVar

}

object BinaryQueryFilter {

  def toOperator(stringOp: String): BinaryOperator = {
    if ("EQUALS".equals(stringOp)) {
      EQUALS
    } else if ("AND".equals(stringOp)) {
      AND
    } else if ("OR".equals(stringOp)) {
      OR
    } else if ("NOT_EQUALS".equals(stringOp)) {
      NOT_EQUALS
    } else if ("LOWER".equals(stringOp)) {
      LOWER
    } else if ("LOWER_OR_EQUAL".equals(stringOp)) {
      LOWER_OR_EQUAL
    } else if ("GREATER".equals(stringOp)) {
      GREATER
    } else if ("GREATER_OR_EQUAL".equals(stringOp)) {
      GREATER_OR_EQUAL
    } else {
      null
    }
  }

}