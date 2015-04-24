package com.github.rosmith.nlp.service.logic

import com.github.rosmith.nlp.query.Query
import com.github.rosmith.nlp.service.datasource.Datasource
import com.github.rosmith.nlp.service.processor.Processor
import com.github.rosmith.nlp.service.util.ServiceUtil
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.util.FileManager
import com.github.rosmith.service.logic.LogicServer
import com.hp.hpl.jena.rdf.model.Model
import com.github.rosmith.nlp.query.model.Data4Storage

class GietServer extends LogicServer {

  private var _datasource: Datasource[Query, Data4Storage, Model] = null

  private var _processor: Processor = null

  private var _model: OntModel = null

  init

  private def init {

    try {
      /*
     * Datasource initialisation
     */
      var datasourceClassName = ServiceUtil.datasource
      if (datasourceClassName == null || datasourceClassName.isEmpty) {
        throw new IllegalArgumentException("The fully qualified name of a datasource should be provided.")
      }
      var datasourceClass = Class.forName(datasourceClassName).asSubclass(classOf[Datasource[Query, Data4Storage, Model]])
      _datasource = datasourceClass.newInstance()
      _datasource.init()

      /*
     * Processor initialisation
     */
      var processorClassName = ServiceUtil.processor
      if (processorClassName == null || processorClassName.isEmpty) {
        throw new IllegalArgumentException("The fully qualified name of a processor should be provided.")
      }
      var processorClass = Class.forName(processorClassName).asSubclass(classOf[Processor])
      _processor = processorClass.newInstance()
      _processor.init()
    } catch {
      case e: Exception => {
        getLogger.error(e.getMessage, e)
      }
    }

  }

  def datasource = _datasource

  def processor = _processor

  def model = _model

}
