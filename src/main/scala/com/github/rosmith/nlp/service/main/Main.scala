package com.github.rosmith.nlp.service.main

import com.github.rosmith.nlp.service.Service
import com.github.rosmith.nlp.query.solution.IQuerySolution

class Main {

}

object Main {

  def main(args: Array[String]): Unit = {

    Service init

    var s = "I'd like to meet Dr. Choi."

    var solution: IQuerySolution = null

    var q = "SELECT ?tokens WHERE { ?x event ?tokens. }"

    solution = Service.process(s, q)

    println(solution.errorMessage)
    solution.print()

    Service disconnect

  }

}
