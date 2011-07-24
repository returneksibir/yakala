package yakala.spiders

import org.jsoup.nodes._

trait Spider {
  def domainName() : String
  def processItem(doc : Document) : Map[String, String]
  def isProductPage(pageUrl : String) : Boolean
}

