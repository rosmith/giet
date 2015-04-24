package com.github.rosmith.nlp.query.filter

  
abstract class SingleQueryFilter extends QueryFilter {
  def isEmpty = false
  
  def clean {}

}