package com.github.rosmith.nlp.service.logic

import java.util.concurrent.Future

import com.github.rosmith.nlp.query.Query
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.query.solution.IQuerySolution

import com.github.rosmith.service.logic.LogicClient
import com.github.rosmith.service.protocol.ProtocolObject

class GietClient extends LogicClient {

  def process(sentence: String, query: String) = {
    var protocol: ProtocolObject = new ProtocolObject("PROCESSING", sentence, query)
    var result = sendProtocol(protocol).asInstanceOf[IQuerySolution]
    result
  }

  private def sendProtocol(protocol: ProtocolObject): Any = {
    getLogger info("SESSION: {}", session)
    var promise: Future[Object] = session write protocol
    var result: Any = promise.get
    getLogger info("Received: {}", result)
    result
  }

}
