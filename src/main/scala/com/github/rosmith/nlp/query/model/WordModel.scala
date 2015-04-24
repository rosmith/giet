package com.github.rosmith.nlp.query.model

class WordModel extends Serializable {

  private var _word: String = null

  private var _lemma: String = null

  private var _tag: String = null

  private var _ner: String = null

  private var _position: Int = -1

  private var _sentencePosition: Int = -1

  private var _typedDep: List[TypedDependencyModel] = List[TypedDependencyModel]()

  private var _references: WordModel = null

  private var _id: String = null

  def word(t: String) = {
    _word = t
    this
  }

  def lemma(l: String) = {
    _lemma = l
    this
  }

  def tag(t: String) = {
    _tag = t
    this
  }

  def ner(n: String) = {
    _ner = n
    this
  }

  def position(p: Int) = {
    _position = p
    this
  }

  def sentencePosition(sP: Int) = {
    _sentencePosition = sP
    this
  }

  def typedDep(td: TypedDependencyModel) = {
    if(!_typedDep.contains(td)) {
      _typedDep = _typedDep :+ td
    }
    this
  }

  def word = _word

  def lemma = _lemma

  def tag = _tag

  def ner = _ner

  def position = _position

  def sentencePosition = _sentencePosition

  def typedDep = _typedDep

  def identity = {
    if(_id == null) {
      _id = Array("Word", position, sentencePosition).mkString("_")
    }
    _id
  }

  def identity(id: String) = {
    _id = id
    this
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[WordModel]) {
      var that = obj.asInstanceOf[WordModel]
      word === that.word &&
        lemma === that.lemma &&
        tag === that.tag &&
        position == that.position &&
        sentencePosition == that.sentencePosition &&
        ((typedDep == null && that.typedDep == null) || typedDep === that.typedDep)
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

  protected implicit class ListImplicit(list: List[TypedDependencyModel]) {
    def ===(that: List[TypedDependencyModel]): Boolean = {
      if (list == null || that == null) {
        true
      } else {
        list.size == that.size &&
        list.map(x => that.contains(x)).filter(x => x == false).size == 0 &&
        that.map(x => list.contains(x)).filter(x => x == false).size == 0
      }
    }
  }

}