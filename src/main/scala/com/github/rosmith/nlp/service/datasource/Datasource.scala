package com.github.rosmith.nlp.service.datasource

import com.github.rosmith.nlp.query.Query
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.query.solution.IQuerySolution
import com.hp.hpl.jena.ontology.OntModel
import org.slf4j.Logger
import com.hp.hpl.jena.rdf.model.Model
import com.github.rosmith.nlp.query.model.Data4Storage

trait Datasource[T <: Query, D <: Data4Storage, M <: Model] {

  def init(): Boolean

  def save(dataForStorage: D, model: M): Boolean

  def save(dataForStorage: D): Boolean

  def executeQuery(query: T): IQuerySolution

  def getLogger: Logger

}