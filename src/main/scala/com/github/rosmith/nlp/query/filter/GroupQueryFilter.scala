package com.github.rosmith.nlp.query.filter

class GroupQueryFilter extends QueryFilter {

  private var groupFilters: List[QueryFilter] = List[QueryFilter]()

  private var _operator: String = null

  def this(filter: QueryFilter) {
    this
    add(filter)
  }

  def changeToOr = {
    _operator = "||"
    this
  }

  def changeToAnd = {
    _operator = "&&"
    this
  }

  def operator = _operator

  def add(filter: QueryFilter): QueryFilter = {
    groupFilters = groupFilters :+ filter
    this
  }

  def clean {
    groupFilters = groupFilters.filter(x => !x.isEmpty)
  }

  def isEmpty = groupFilters.isEmpty

  def filters = groupFilters

}