package com.github.rosmith.nlp.exception

class NoInferenceException extends Exception {

  override def getMessage = {
    "The received query was not infered"
  }
  
}