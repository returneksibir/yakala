package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import collection.mutable.Set
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger, spider : Spider, pipeline : ItemPipeline) extends Actor {
  
  private var setOfLinksAlreadyVisited : Set[String] = Set()

  def act() {
    loop {
      react {
        case item : Map[String, String] =>
          pipeline.processItem(item)
        case url : String =>
          if (!setOfLinksAlreadyVisited.contains(url)) {
            spider ! url
            setOfLinksAlreadyVisited  += url
            logger.debug("Gezilen   sayfa sayısı : " + setOfLinksAlreadyVisited.size)
          }
        case _ => 
          require(false)
      }
    }
  }
}

