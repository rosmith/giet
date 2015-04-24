package com.github.rosmith.nlp.query.model

import java.io.Serializable

class AnnotatedSentence(sen: String) extends Data4Storage {

  private val _sentence = sen

  private var _words: Array[WordModel] = null

  private var _td: Array[TypedDependencyModel] = null

  private var _corefs: Array[CorefModel] = Array()

  private var _srl: Array[SemanticRoleFrameModel] = Array()

  def sentence = _sentence

  def words = _words

  def words(t: Array[WordModel]) = _words = t

  def typedDependencies = _td

  def typedDependencies(td: Array[TypedDependencyModel]) = _td = td

  def corefs = _corefs

  def corefs(c: Array[CorefModel]) = _corefs = c

  def srl(s: Array[SemanticRoleFrameModel]) = _srl = s

  def srl = _srl

  override def toString: String = {
    var result: StringBuffer = new StringBuffer
    if (typedDependencies != null) {
      for(i <- 0 to typedDependencies.length - 1){
        result append '\n'
        result append '\t'
        result append typedDependencies.apply(i)
        result append ','
      }
      result.deleteCharAt(result.length - 1)
      result append '\n'
    }
    result toString
  }

}