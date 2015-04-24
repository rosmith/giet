package com.github.rosmith.nlp.query

abstract class NormalQueryStatement(s: String, o: String, oIsVar: Boolean) extends Statement {

  private var _subj = s

  private var _obj = o

  private var _oIsVar = oIsVar

  def this(s: String, o: String) {
    this(s, o, false)
  }

  def statementSubject(s: String) {
    this._subj = s
  }

  def statementObject(o: String) {
    this._obj = o
  }

  def statementPredicate(p: String) {}

  def statementSubject = _subj

  def statementObject = _obj

  def statementPredicate: String

  def objectIsVariable(oiv: Boolean) {
    this._oIsVar = oiv
  }

  def objectIsVariable = oIsVar

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[NormalQueryStatement]) {
      var stmt = obj.asInstanceOf[NormalQueryStatement]
      stmt.statementSubject.equals(s) && stmt.statementObject.equals(o)
    } else false
  }

}