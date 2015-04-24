package com.github.rosmith.nlp.service.store

import java.io.Serializable
import com.github.rosmith.nlp.query.model.Data4Storage

class TripleStore extends Data4Storage {

  private var store: List[Triple] = List[Triple]()

  def addTriple(triple: Triple) {
    store = store :+ triple
  }

  def removeTriple(triple: Triple) {
    store = store.filter(s => !s.equals(triple))
  }

  def triples = store

  override def toString = {
    var result = ""
    for(i <- 0 to store.size-1){
      result += store.apply(i).toString + "\n"
    }
    result.trim
  }

}