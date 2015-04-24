package com.github.rosmith.nlp.query

class QueryStatement(s: String, p: String, o: String, oIsVar: Boolean) extends NormalQueryStatement(s, o, oIsVar) {

  private var _pred = p

  def this(s: String) {
    this(s, null, null, true)
  }

  def this(s: String, p: String) {
    this(s, p, null, true)
  }

  def this(s: String, p: String, o: String) {
    this(s, p, o, true)
  }

  override def statementPredicate(p: String) {
    this._pred = p
  }

  def statementPredicate = _pred

  override def equals(obj: Any) = {
    if (super.equals(obj) && obj.isInstanceOf[QueryStatement]) {
      var stmt = obj.asInstanceOf[QueryStatement]
      stmt.statementPredicate.equals(p)
    } else false
  }

}