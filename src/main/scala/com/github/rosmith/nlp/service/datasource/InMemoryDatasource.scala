package com.github.rosmith.nlp.service.datasource

import com.github.rosmith.nlp.query.SparQLQuery
import com.github.rosmith.nlp.query.solution.IQuerySolution
import com.github.rosmith.nlp.service.store.TripleStore
import com.github.rosmith.nlp.service.util.ServiceUtil._
import com.github.rosmith.nlp.exception.NoInferenceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.datatypes.RDFDatatype
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.ReadWrite
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.ModelMaker

class InMemoryDatasource extends Datasource[SparQLQuery, TripleStore, Model] {

  private var model: Model = null

  private var modelMaker: ModelMaker = null

  def init(): Boolean = {
    modelMaker = ModelFactory.createMemModelMaker()
    model = modelMaker.createDefaultModel()
    true
  }

  def save(dataForStorage: TripleStore) = {
    init
    dataForStorage.triples.foreach(triple => {
      var res = resource(triple.subj)
      var prop = property(triple.pred)
      var node = if (triple.objectIsResource) resource(triple.obj) else literal(triple.obj)
      var statement = model.createStatement(res, prop, node)
      model.add(statement)
    })
    true
  }

  def save(dataForStorage: TripleStore, model: Model): Boolean = {
    true
  }

  def executeQuery(serviceQuery: SparQLQuery): IQuerySolution = {
    if (!serviceQuery.isInfered) {
      throw new NoInferenceException
    }

    println(serviceQuery.prettyStringify)

    printModel

    var query = QueryFactory.create(serviceQuery.stringify)
    var qexec = QueryExecutionFactory.create(query, model)
    try {
      var results = qexec.execSelect()
      createQuerySolution(results, serviceQuery.variables.map(v => v.value))
    } catch {
      case e: Exception => {
        e.printStackTrace
        throw e
      }
    } finally {
      qexec.close();
    }
  }

  def getLogger: Logger = LoggerFactory.getLogger(classOf[InMemoryDatasource])

  def printModel {
    println("##############################################")
    println("#          Model-Entries")
    println("##############################################")
    var itr = model.listStatements()
    while (itr.hasNext()) {
      var statement = itr.nextStatement()
      var res = statement.getSubject.getLocalName
      var prop = statement.getPredicate.getLocalName
      var node = if (statement.getObject.isResource()) statement.getObject.asResource().getLocalName else statement.getObject.asLiteral().getValue

      println(Array(res, prop, node).mkString(" => "))
    }
    println("##############################################")
  }

}