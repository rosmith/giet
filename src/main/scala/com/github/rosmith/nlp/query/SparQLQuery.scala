package com.github.rosmith.nlp.query

import com.hp.hpl.jena.ontology.OntModel
import com.github.rosmith.nlp.query.filter.QueryFilter
import com.github.rosmith.nlp.query.filter.RegexQueryFilter
import com.github.rosmith.nlp.query.filter.DatatypeQueryFilter
import com.github.rosmith.nlp.query.filter.CountQueryFilter
import com.github.rosmith.nlp.query.filter.BinaryQueryFilter
import com.github.rosmith.nlp.query.filter.GroupQueryFilter
import com.github.rosmith.nlp.query.filter.BinaryOperator._
import com.github.rosmith.nlp.query.filter.Datatype._
import com.github.rosmith.nlp.query.util.Helper
import com.github.rosmith.nlp.service.util.ServiceUtil._

class SparQLQuery(distinct: Boolean) extends Query(distinct) {

  infered = true

  def this() {
    this(false)
  }

  def prettyStringify = {
    var buffer = new StringBuffer

    prefixes.foreach(x => {
      buffer.
        append(Array("PREFIX ", x.namespaceVariable, ": <", x.namespace, ">").mkString("")).
        append("\n")
    })

    buffer.append("\n")

    var distinctVar = if (isDistinct) "DISTINCT " else ""
    buffer.
      append("SELECT ").
      append(distinctVar)

    variables.foreach(x => {
      buffer.
        append(Array("?", x.value).mkString("")).
        append(" ")
    })

    buffer.
      append("\n").
      append("WHERE {").
      append("\n")

    statements.foreach(x => {
      var _object = {
        if (!x.isInstanceOf[IsAQueryStatement]) {
          if (x.objectIsVariable)
            Array("?", x.statementObject).mkString("")
          else
            Array("\"", x.statementObject, "\"").mkString("")
        } else {
          x.statementObject
        }
      }

      buffer.
        append("\t").
        append(Array("?", x.statementSubject).mkString("")).
        append(" ").
        append(x.statementPredicate).
        append(" ").
        append(_object).
        append(" .").
        append("\n")
    })

    filters.foreach(x => {
      buffer.
        append("\t").
        append("FILTER ").
        append(x.stringify).
        append("\n")
    })

    buffer.append("}")

    buffer.toString
  }

  def inferType(model: OntModel) {
    infered = true
  }

  private implicit class FilterToString(filter: QueryFilter) {
    def stringify: String = {
      filter match {
        case x: RegexQueryFilter => {
          var option = if (x.hasOption) ", \""+x.option+"\")" else ")"
          Array("REGEX(", "?", x.variable, ", \"", x.regex, "\"", option).mkString("")
        }
        case x: DatatypeQueryFilter => {
          var datatype = x.datatype match {
            case "INTEGER" => xsd("int")
            case "FLOAT" => xsd("float")
            case "LONG" => xsd("long")
            case "DOUBLE" => xsd("double")
            case "BOOLEAN" => xsd("boolean")
            case _ => xsd("string")
          }
          Array("DATATYPE(", "?", x.leftVariable, ") = ", datatype).mkString("")
        }
        case x: CountQueryFilter => {
          var rightValue = if (x.rightValueIsVariable) Array("?", x.rightVariable).mkString("") else x.rightVariable
          Array("COUNT(", "?", x.leftVariable, ")", operator(x.operator), rightValue).mkString("")
        }
        case x: BinaryQueryFilter => {
          var clzz = Helper.determineType(x.rightVariable)
          var rightValue = Helper.convert(clzz, x.rightVariable)
          if (clzz == classOf[String]) {
            rightValue = Array(rightValue).mkString("\"", "", "\"")
          }

          rightValue = if (x.rightValueIsVariable) Array("?", x.rightVariable).mkString("") else rightValue
          Array("?", x.leftVariable, operator(x.operator), rightValue).mkString("")
        }
        case x: GroupQueryFilter => {
          printGroupQuery(x)
        }
        case _ => {
          ""
        }
      }
    }

  }

  private def printGroupQuery(x: GroupQueryFilter): String = {
    if (x.filters.size == 1 && x.filters.apply(0) != null && x.filters.apply(0).isInstanceOf[GroupQueryFilter]) {
      var first = x.filters.apply(0)
      printGroupQuery(first.asInstanceOf[GroupQueryFilter])
    } else {
      var result = Array("( ")
      for (i <- 0 to x.filters.size - 1) {
        var y = x.filters.apply(i)
        if (y.isInstanceOf[GroupQueryFilter]) {
          result = result :+ printGroupQuery(y.asInstanceOf[GroupQueryFilter])
        } else {
          result = result :+ y.stringify
        }
        if (i + 1 != x.filters.size) {
          result = result :+ " " + x.operator + " "
        }
      }
      result = result :+ " )"
      result.mkString("")
    }
  }

  private def operator(op: BinaryOperator): String = {
    op match {
      case EQUALS => " = "
      case NOT_EQUALS => " != "
      case LOWER => " < "
      case LOWER_OR_EQUAL => " <= "
      case GREATER => " > "
      case GREATER_OR_EQUAL => " >= "
      case _ => ""
    }
  }

}