package com.github.rosmith.nlp.service.processor

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import org.slf4j.Logger

trait Processor {

  def init(): Boolean

  def process(sentence: String): AnnotatedSentence

  def getLogger: Logger

}