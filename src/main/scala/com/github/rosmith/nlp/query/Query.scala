package com.github.rosmith.nlp.query

import com.github.rosmith.nlp.query.filter.QueryFilter
import com.github.rosmith.nlp.query.filter.QueryFilter
import com.hp.hpl.jena.ontology.OntModel
import com.github.rosmith.nlp.query.filter.QueryFilter
import com.github.rosmith.nlp.query.filter.GroupQueryFilter

abstract class Query(d: Boolean) extends Serializable {

  private var resultVariables: List[Variable] = List[Variable]()

  private var _statements: List[NormalQueryStatement] = List[NormalQueryStatement]()

  private var _filters: List[QueryFilter] = List[QueryFilter]()

  private var _prefixes: List[PrefixStatement] = List[PrefixStatement]()

  protected var infered = false

  private var _distinct = d

  def this() {
    this(false)
  }

  def setDistinct(isDistinct: Boolean) {
    _distinct = isDistinct
  }

  def addVariable(variable: Variable) {
    if (!resultVariables.contains(variable)) {
      resultVariables = resultVariables :+ variable
    }
  }

  def addStatement(statement: NormalQueryStatement) {
    if (!_statements.contains(statement)) {
      _statements = _statements :+ statement
    }
  }

  def addPrefixStatement(prefix: PrefixStatement) {
    if (!_prefixes.contains(prefix)) {
      _prefixes = _prefixes :+ prefix
    }
  }

  def addFilter(filter: QueryFilter) {
    _filters = _filters :+ filter
  }

  def updateFilter(i: Int, filter: QueryFilter) {
    _filters = _filters.updated(i, filter)
  }

  def cleanFilters() {
    _filters.foreach(x => x.clean)
    _filters = _filters.filter(x => !x.isEmpty)
  }

  def contains(variable: String) = {
    resultVariables.contains(new Variable(variable))
  }

  def removeStatement(stmt: NormalQueryStatement) {
    _statements = _statements.filter(s => !s.equals(stmt))
  }

  def inferType(model: OntModel)

  def variables = resultVariables

  def statements = _statements

  def prefixes = _prefixes

  def filters = _filters

  def prettyStringify: String

  def isInfered = infered

  def isDistinct = _distinct

  def stringify = {
    if (prettyStringify != null)
      prettyStringify.replaceAll("\n", " ").replaceAll("\t", " ")
    else ""
  }

}