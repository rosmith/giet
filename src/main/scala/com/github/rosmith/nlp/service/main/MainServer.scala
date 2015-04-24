package com.github.rosmith.nlp.service.main

import java.io.File
import com.github.rosmith.nlp.service.util.ServiceUtil
import com.github.rosmith.service.logic.LogicServer
import java.io.InputStream
import java.io.FileInputStream
import com.github.rosmith.nlp.service.logic.GietServer

class MainServer {

}

object MainServer {

  private var serviceConfig: InputStream = null

  def main(args: Array[String]): Unit = {
    start()
    try {
      for (i <- 0 to args.length - 1) {
        if (args.apply(i).equals("-s")) {
          serviceConfig = new FileInputStream(args.apply(i + 1))
        }
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        usage
      }
    }

    if (serviceConfig != null) {
      init
    } else {
      load
    }
  }

  private def init() {
    ServiceUtil.init(serviceConfig)
    LogicServer.setSingletonClass(classOf[GietServer])
    com.github.rosmith.service.server.Server execute
  }

  private def load() {
    serviceConfig = classOf[MainServer].getClassLoader().getResourceAsStream("service.properties")
    if (serviceConfig == null) {
      usage
    } else {
      init
    }
  }

  def start() {
    println("# Extractor Service - version 2")
    println("# Author: Djomkam Yotedje Ronald Smith")
  }

  def usage() {
    println("# Usage")
    println("java [JAVA_OPTS] -jar giet.jar options")
    println()
    println("options: ")
    println("-s [FILE_NAME] # This is the service.properties file ")
  }

}