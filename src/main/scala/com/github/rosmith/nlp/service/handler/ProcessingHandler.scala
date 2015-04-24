package com.github.rosmith.nlp.service.handler

import java.util.HashSet
import java.util.Set
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Function
import scala.collection.mutable.Map
import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.query.model.WordModel
import com.github.rosmith.nlp.service.extractors.Extractor
import com.github.rosmith.nlp.service.extractors.Plugin
import com.github.rosmith.nlp.service.extractors.Requirement
import com.github.rosmith.nlp.service.logic.GietServer
import com.github.rosmith.nlp.service.parser.Syntax
import com.github.rosmith.nlp.service.processor.Processor
import com.github.rosmith.nlp.service.store.TripleStore
import com.github.rosmith.nlp.service.util.ServiceUtil
import com.github.rosmith.service.handler.impl.AbstractDefaultHandler
import com.github.rosmith.service.protocol.ProtocolObject
import com.github.rosmith.service.scanner.HandlerScanner
import com.github.rosmith.nlp.service.datasource.Datasource
import com.github.rosmith.nlp.query.model.Data4Storage
import com.hp.hpl.jena.rdf.model.Model
import com.github.rosmith.nlp.query.Query
import com.github.rosmith.service.annotation.Handler
import com.github.rosmith.nlp.query.solution.QuerySolutionImpl
import java.util.TreeSet

import com.github.rosmith.nlp.service.util.ImplicitUtil._

@Handler(protocol = "PROCESSING")
class ProcessingHandler(protocol: String) extends AbstractDefaultHandler(protocol) {

  private var processor: Processor = null

  private var datasource: Datasource[Query, Data4Storage, Model] = null

  def serverHandler = {
    new Function[ProtocolObject, Object]() {
      def apply(p: ProtocolObject) = {
        try {
          var sentence = p.getObject().apply(0).asInstanceOf[String]
          var query = p.getObject().apply(1).asInstanceOf[String]
          handle(query, sentence)
        } catch {
          case e: Exception => {
            getLogger.error(e.getMessage, e)
            new QuerySolutionImpl
          }
        }
      }
    }
  }

  private def handle(q: String, s: String) = {
    var query = Syntax.parse(q, false)
    var set: Set[Class[_]] = new HashSet()

    ServiceUtil.extractorsPackage.foreach(packageName => {
      set addAll HandlerScanner.scan(packageName, classOf[Plugin])
    })

    var list = List[Class[_]]()

    var itr = set.iterator()

    while (itr.hasNext()) {
      list = list :+ itr.next()
    }

    var map = Map[Plugin, Extractor]()

    var neededNamespaces = query.statements.map(stmt => stmt.statementPredicate.split(":").apply(1))

    neededNamespaces.foreach(ns => {
      list.foreach(extractorClass => {
        var plugin = extractorClass.getAnnotation(classOf[Plugin])
        var inst = extractorClass.newInstance().asInstanceOf[Extractor]
        if (plugin.multirelation()) {
          if (ns.equals(plugin.namespace()) || inst.hasRelation(ns)) {
            map.put(plugin, inst)
          }
        } else {
          if (ns.equals(plugin.namespace())) {
            map.put(plugin, inst)
          }
        }
      })
    })

    //    var listOfRelationsWithoutExtractors = query.statements.filter(stmt => !map.keySet.contains(stmt.statementPredicate.split(":").apply(1)))
    //
    //    listOfRelationsWithoutExtractors.foreach(stmt => query.removeStatement(stmt))

    if (processor == null) {
      processor = logic.asInstanceOf[GietServer].processor
    }

    var usePredefinedProcessor = map.filter {
      case (plugin, value) => {
        plugin.usePredefinedProcessor()
      }
    }

    if (usePredefinedProcessor.size > 0) {
      var annotatedSentence = processor.process(s)
      usePredefinedProcessor.foreach {
        case (plugin, extractor) => {
          var source = annotatedSentence.words
          var target = Array[WordModel]()
          source.foreach(w => {
            target = target :+ new WordModel().identity(w.identity)
          })

          var document = new AnnotatedSentence(s)

          extractor.requirements.foreach(requirement => {
            if (requirement.equals(Requirement.DEPENDENCY)) {
              document.typedDependencies(annotatedSentence.typedDependencies)
            } else if (requirement.equals(Requirement.COREF)) {
              document.corefs(annotatedSentence.corefs)
            } else if (requirement.equals(Requirement.SRL)) {
              document.srl(annotatedSentence.srl)
            } else {
              fillWordModelAccordingToRequirement(source, target, requirement)
              document.words(target)
            }
          })

          extractor.setDocument(document)
        }
      }
    }

    var notUsePredefinedProcessor = map.filter {
      case (plugin, extractor) => {
        !plugin.usePredefinedProcessor()
      }
    }

    notUsePredefinedProcessor.foreach {
      case (plugin, extractor) => {
        var document = new AnnotatedSentence(s)
        extractor.setDocument(document)
      }
    }

    var futures = List[Future[java.lang.Boolean]]()

    var service = Executors.newFixedThreadPool(if (map.size == 0) 1 else map.size)

    map.foreach {
      case (plugin, extractor) => {
        futures = futures :+ service.submit(extractor)
      }
    }

    futures.foreach(f => {
      f.get
    })

    service.shutdown()

    var store = new TripleStore

    map.foreach {
      case (plugin, extractor) => {
        extractor.register(store)
      }
    }

    map.foreach {
      case (plugin, extractor) => {
        query.statements.foreach(stmt => {
          if (plugin.multirelation() && extractor.hasRelation(stmt.statementPredicate.split(":").apply(1))) {
            var pred = stmt.statementPredicate
            var prefix = pred.split(":").apply(0)
            pred = if (plugin.namespace().equals(pred.split(":").apply(1))) plugin.namespace() else Array(plugin.namespace(), pred.split(":").apply(1)).mkString("_")
            pred = Array(prefix, pred).mkString(":")
            stmt.statementPredicate(pred)
          }
        })
      }
    }

    if (datasource == null) {
      datasource = logic.asInstanceOf[GietServer].datasource
    }

    datasource.save(store)

    datasource.executeQuery(query)
  }

  private def fillWordModelAccordingToRequirement(source: Array[WordModel], target: Array[WordModel], requirement: Requirement) = {
    for (i <- 0 to source.size - 1) {
      if (requirement.equals(Requirement.WORD)) {
        target.apply(i).word(source.apply(i).word)
      } else if (requirement.equals(Requirement.LEMMA)) {
        target.apply(i).lemma(source.apply(i).lemma)
      } else if (requirement.equals(Requirement.TAG)) {
        target.apply(i).tag(source.apply(i).tag)
      } else if (requirement.equals(Requirement.NE)) {
        target.apply(i).ner(source.apply(i).ner)
      } else if (requirement.equals(Requirement.POSITION)) {
        target.apply(i).position(source.apply(i).position)
        target.apply(i).sentencePosition(source.apply(i).sentencePosition)
      }
    }
  }

}
