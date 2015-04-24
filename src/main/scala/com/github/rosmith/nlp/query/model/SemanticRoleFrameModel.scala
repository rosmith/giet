package com.github.rosmith.nlp.query.model

class SemanticRoleFrameModel extends Serializable {

  private var _position: Int = -1

  private var _elements: List[SemanticRoleElementModel] = List()

  def addElement(elem: SemanticRoleElementModel) {
    elem.sentPos(_position)
    if (!_elements.contains(elem)) {
      _elements = _elements :+ elem
    }
  }

  def elements = _elements

  def position(p: Int) = _position = p

  def identity = {
    Array("Frame", _position, elements.size, super.toString.split("@").apply(1)).mkString("_")
  }

  override def equals(obj: Any) = {
    if (obj != null && obj.isInstanceOf[SemanticRoleFrameModel]) {
      var elem = obj.asInstanceOf[SemanticRoleFrameModel]
      if (identity == null || elem.identity == null) {
        false
      } else {
        identity.equals(elem.identity)
      }
    } else {
      false
    }
  }

}