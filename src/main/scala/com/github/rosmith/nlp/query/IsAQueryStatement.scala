package com.github.rosmith.nlp.query

class IsAQueryStatement(s: String, o: String) extends NormalQueryStatement(s, o) {

  def statementPredicate = "a"

}