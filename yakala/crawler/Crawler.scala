package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import collection.mutable.Set
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger, pipeline : ItemPipeline) extends Actor {
  
  private var setOfLinksAlreadyVisited : Set[String] = Set()

  def crawlPage(spider : Spider, url : String)  {
    val url_ = url.toLowerCase()
    if (!setOfLinksAlreadyVisited.contains(url_)) {
      spider ! url
      setOfLinksAlreadyVisited  += url_
      logger.debug("Gezilen   sayfa sayısı : " + setOfLinksAlreadyVisited.size)
    }
  }

  def act() {
    loop {
      react {
        case item : Map[String, String]       => pipeline.processItem(item)
        case (spider : Spider, url : String)  => crawlPage(spider, url)
        case _                                => require(false)
      }
    }
  }
}

