package com.github.rosmith.nlp.service

import com.github.rosmith.nlp.service.logic.GietClient
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.query.solution.IQuerySolution
import com.github.rosmith.nlp.query.Query

object Service {

  private var logic: GietClient = null

  def init() {
    logic = new GietClient
  }

  def process(sentence: String, query: String) = {
    var result = logic.process(sentence, query)
    result
  }

  def disconnect() {
    logic stop;
    logic = null
  }

}
