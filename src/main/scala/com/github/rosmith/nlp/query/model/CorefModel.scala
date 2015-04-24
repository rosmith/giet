package com.github.rosmith.nlp.query.model

import java.io.Serializable

class CorefModel extends Serializable {

  private var _references: CorefModel = null

  private var _value: String = null

  private var _position: Int = -1

  private var _sentencePosition: Int = -1

  def references(r: CorefModel) = _references = r

  def position(p: Int) = _position = p

  def value(s: String) = _value = s

  def sentencePosition(sP: Int) = _sentencePosition = sP

  def references = _references

  def value = _value

  def position = _position

  def sentencePosition = _sentencePosition

  def identity = {
    Array("Coreference", position, sentencePosition).mkString("_")
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[CorefModel]) {
      var that = obj.asInstanceOf[CorefModel]
      value === that.value &&
        position == that.position &&
        sentencePosition == that.sentencePosition &&
        ((references == null && that.references == null) || references.identity == that.identity)
    } else {
      false
    }
  }

  protected implicit class StringImplicit(str: String) {
    def ===(that: String): Boolean = {
      if (str == null || that == null) {
        false
      } else {
        str.equals(that)
      }
    }
  }

}