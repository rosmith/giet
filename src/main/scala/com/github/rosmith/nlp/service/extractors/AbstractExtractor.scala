package com.github.rosmith.nlp.service.extractors

import java.util.concurrent.Callable
import com.github.rosmith.nlp.service.store.TripleStore
import com.github.rosmith.nlp.service.store.Triple
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable.Buffer

abstract class AbstractExtractor extends Extractor {

  private var triples: Buffer[Triple] = null

  private var _document: AnnotatedSentence = null

  def setDocument(document: AnnotatedSentence) {
    _document = document
  }

  def call: java.lang.Boolean = {
    triples = extract(_document).asScala
    triples != null && triples.size > 0
  }

  override def toString = {
    triples.map(x => x.toString).mkString("[", ", ", "]")
  }

  def register(store: TripleStore) {
    val triples = this.triples
    for (i <- 0 to triples.size-1) {
      store.addTriple(triples apply i)
    }
  }

  def getLogger: Logger = LoggerFactory.getLogger(getClass)

}
