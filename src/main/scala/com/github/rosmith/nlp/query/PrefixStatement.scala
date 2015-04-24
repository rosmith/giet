package com.github.rosmith.nlp.query

class PrefixStatement(nsVar: String, ns: String) extends Statement {
  
  assert(nsVar != null && !nsVar.isEmpty)
  assert(ns != null && !ns.isEmpty)
  
  def namespaceVariable = nsVar
  
  def namespace = ns
  
  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[PrefixStatement]) {
      var stmt = obj.asInstanceOf[PrefixStatement]
      stmt.namespaceVariable.equals(nsVar) && stmt.namespace.equals(ns)
    } else false
  }

}