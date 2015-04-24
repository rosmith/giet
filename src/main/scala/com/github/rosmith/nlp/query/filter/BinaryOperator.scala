package com.github.rosmith.nlp.query.filter

final object BinaryOperator extends Enumeration {
  
  type BinaryOperator = Value
  
  val AND, OR, EQUALS, NOT_EQUALS, LOWER, LOWER_OR_EQUAL, GREATER, GREATER_OR_EQUAL = Value

}
