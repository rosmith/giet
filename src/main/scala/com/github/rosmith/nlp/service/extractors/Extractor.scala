package com.github.rosmith.nlp.service.extractors

import java.util.concurrent.Callable
import java.util.List

import com.github.rosmith.nlp.service.store.TripleStore
import com.github.rosmith.nlp.service.store.Triple
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import org.slf4j.Logger

trait Extractor extends Callable[java.lang.Boolean] {

  def extract(document: AnnotatedSentence): List[Triple]

  def register(store: TripleStore): Unit

  def hasRelation(reln: String): Boolean

  def requirements(): List[Requirement]

  def setDocument(document: AnnotatedSentence): Unit

  def getLogger: Logger

}