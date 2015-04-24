package com.github.rosmith.nlp.query.filter

import com.github.rosmith.nlp.query.OperandValue

class RegexQueryFilter(_variable: OperandValue, _regex: String, _option: String) extends SingleQueryFilter {
  
  private var _hasOption = _option != null && !_option.isEmpty()
  
  def this(_variable: OperandValue, _regex: String) {
    this(_variable, _regex, "")
  }
  
  def variable = _variable.value
  
  def regex = _regex
  
  def hasOption = _hasOption
  
  def option = _option

}