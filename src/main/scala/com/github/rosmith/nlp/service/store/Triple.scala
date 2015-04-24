package com.github.rosmith.nlp.service.store

import java.io.Serializable

class Triple(oIR: Boolean) extends Serializable {

  /**
   * Represents the subject of this triple.
   */
  private var subject: String = null

  /**
   * Represents the predicate of this triple.
   */
  private var predicate: String = null

  /**
   * Represents the object of this triple.
   */
  private var _object: String = null

  def this() {
    this(false)
  }

  /*
   *      GETTERS & SETTERS
   *************************************/

  def subj = subject

  def pred = predicate

  def obj = _object

  def subj_(s: String) = {
    subject = s
    this
  }

  def pred_(p: String) = {
    predicate = p
    this
  }

  def obj_(o: String) = {
    _object = o
    this
  }

  def objectIsResource = oIR

  override def equals(_obj: Any) = {
    if (_obj != null && _obj.isInstanceOf[Triple]) {
      var that = _obj.asInstanceOf[Triple]
      subj === that.subj &&
        pred === that.pred &&
        obj === that.obj
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

  override def toString = {
    "(subject=" + subject + ", predicate=" + predicate + ", object=" + _object + ")"
  }

}