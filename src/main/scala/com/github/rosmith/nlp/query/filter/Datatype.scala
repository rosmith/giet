package com.github.rosmith.nlp.query.filter

final object Datatype extends Enumeration {
  
  type Datatype = Value
  
  val INTEGER, FLOAT, LONG, DOUBLE, BOOLEAN, STRING = Value
  
  def toString(dT: Datatype): String = {
    if(dT == INTEGER){
      "INTEGER"
    }
    if(dT == FLOAT){
      "FLOAT"
    }
    if(dT == LONG){
      "LONG"
    }
    if(dT == DOUBLE){
      "DOUBLE"
    }
    if(dT == BOOLEAN){
      "BOOLEAN"
    }
    if(dT == STRING){
      "STRING"
    }
    null
  }
  
}