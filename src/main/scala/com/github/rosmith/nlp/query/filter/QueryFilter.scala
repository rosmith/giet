package com.github.rosmith.nlp.query.filter

trait QueryFilter extends Serializable {
  
  def isEmpty: Boolean
  
  def clean
  
}