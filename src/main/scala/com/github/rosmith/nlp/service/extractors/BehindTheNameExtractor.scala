package com.github.rosmith.nlp.service.extractors

import java.text.MessageFormat
import java.{ util => ju }
import scala.collection.JavaConverters._

import com.github.rosmith.nlp.query.model.AnnotatedSentence
import com.github.rosmith.nlp.service.store.Triple
import org.jsoup.Jsoup
import scala.util.control.Breaks._

import com.github.rosmith.nlp.service.util.ImplicitUtil._

import us.codecraft.xsoup.Xsoup

@Plugin(namespace = "btn", multirelation = true)
class BehindTheNameExtractor extends AbstractExtractor {

  private val URL = "http://www.behindthename.com/name/{0}"

  private val relations = List("gender", "definition")

  def extract(doc: AnnotatedSentence): ju.List[Triple] = {
    var list = List[Triple]()
    doc.words.filter(w => w.ner.equals("PERSON")).foreach(w => {
      val doc = Jsoup.connect(MessageFormat.format("http://www.behindthename.com/name/{0}", "smith")).get
      val gender = doc.select("div.namesub").filter(_.select("span.namesub").text().equals("GENDER:")).map(e => e.select("span.info").get(0)).get(0).text()

      var definition = Xsoup.compile("//div[@class='body']/div[3]/div[6]/text()").evaluate(doc).get();

      if (gender != null || !gender.isEmpty()) {
        list = list :+ new Triple().subj_(w.identity).pred_(Array("btn", "gender").mkString("_")).obj_(gender)
      }

      if (definition != null || !definition.isEmpty()) {
        list = list :+ new Triple().subj_(w.identity).pred_(Array("btn", "definition").mkString("_")).obj_(definition)
      }
    })
    list.asJava
  }

  def hasRelation(reln: String) = {
    relations.contains(reln)
  }

  def requirements(): ju.List[Requirement] = {
    List(Requirement.WORD, Requirement.NE).asJava
  }

}