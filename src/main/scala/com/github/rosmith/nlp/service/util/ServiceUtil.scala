package com.github.rosmith.nlp.service.util

import java.util.Properties
import java.io.InputStreamReader
import java.io.FileInputStream
import java.util.function.Consumer
import com.github.rosmith.nlp.query.util.Helper
import com.github.rosmith.nlp.query.solution.QuerySolutionImpl
import com.github.rosmith.nlp.query.solution.IQuerySolution
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.QuerySolution
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.datatypes.RDFDatatype
import java.io.InputStream
import com.github.rosmith.service.logic.LogicServer
import com.github.rosmith.nlp.service.logic.GietServer

class ServiceUtil {

}

object ServiceUtil {

  def xsd(param: String) = Array(XSD_NAMESPACE_VARIABLE, ":", param).mkString("")

  def XSD_NAMESPACE_VARIABLE = "xsd"

  def XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#"

  def NAMESPACE = "http://www.giet.com/annotator#"

  def NAMESPACE_VARIABLE = "giet"

  def namespace(param: String) = Array(NAMESPACE_VARIABLE, ":", param).mkString("")

  private val properties = new Properties

  def init(config: String) {
    properties.load(new InputStreamReader(new FileInputStream(config)))
    LogicServer.setSingletonClass(classOf[GietServer])
  }
  
  def init(stream: InputStream) {
    properties.load(new InputStreamReader(stream))
    LogicServer.setSingletonClass(classOf[GietServer])
  }

  def datasource = "com.github.rosmith.nlp.service.datasource.InMemoryDatasource"

  def processor = "com.github.rosmith.nlp.service.processor.StanfordProcessor"

  def dict = properties getProperty "com.github.rosmith.nlp.service.clearnlp.dict"

  def pos = properties getProperty "com.github.rosmith.nlp.service.clearnlp.pos"

  def pred = properties getProperty "com.github.rosmith.nlp.service.clearnlp.pred"

  def role = properties getProperty "com.github.rosmith.nlp.service.clearnlp.role"

  def dep = properties getProperty "com.github.rosmith.nlp.service.clearnlp.dep"

  def srl = properties getProperty "com.github.rosmith.nlp.service.clearnlp.srl"

  def extractorsPackage = {
    val EXTRACTOR_PACKAGES = "com.github.rosmith.nlp.service.extractors"
    var packages = properties getProperty "com.github.rosmith.nlp.service.extractors.packages"
    if (packages == null || packages.isEmpty) {
      Array(EXTRACTOR_PACKAGES)
    } else {
      packages.split(",") :+ EXTRACTOR_PACKAGES
    }
  }

  def createQuerySolution(resultSet: ResultSet, variables: List[String]): IQuerySolution = {
    var solutions = List[QuerySolution]()
    while (resultSet.hasNext) {
      solutions = solutions :+ resultSet.nextSolution
    }

    var map = scala.collection.mutable.Map[String, List[Any]]()

    var classMap = scala.collection.mutable.Map[String, Class[_]]()

    if (solutions != null && solutions.size > 0) {
      variables foreach (v => {
        var list = List[Any]()
        solutions foreach (s => {
          var node = s.get(v)
          var value: Any = null
          var clzz: Class[_] = null
          if (node != null) {
            if (node.isLiteral()) {
              var lit = node.asInstanceOf[Literal]
              clzz = lit.getDatatype.getJavaClass
              value = Helper.convert(clzz, lit getValue)
            } else if (node.isResource()) {
              var res = node.asInstanceOf[Resource]
              var index = res.toString.lastIndexOf("#")
              value = res.toString.substring(index + 1)
              clzz = classOf[String]
            }
          } else {
            clzz = classOf[String]
            value = "-"
          }
          list = list :+ value
          classMap.put(v, clzz)
        })
        map.put(v, list)
      })

      new QuerySolutionImpl(map, classMap, variables)
    } else {
      new QuerySolutionImpl
    }
  }

  def resource(localname: String): Resource = {
    ResourceFactory.createResource(Array(NAMESPACE, localname).mkString("", "", ""));
  }

  def property(localname: String): Property = {
    ResourceFactory.createProperty(NAMESPACE, localname);
  }

  def literal(value: Object): Literal = {
    ResourceFactory.createTypedLiteral(value);
  }

  def literal(lexicalform: String, datatype: RDFDatatype): Literal = {
    ResourceFactory.createTypedLiteral(lexicalform, datatype);
  }

}