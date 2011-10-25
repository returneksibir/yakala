package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import collection.mutable.HashSet
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger, pipeline : ItemPipeline) extends Actor {
  
  private var setOfLinksAlreadyVisited : HashSet[String] = HashSet()
  private var numberOfDuplicateLinks = 0

  def crawlPage(spider : Spider, url : String)  {
    val url_ = url.toLowerCase()
    if (!setOfLinksAlreadyVisited.contains(url_)) {
      spider ! url
      setOfLinksAlreadyVisited  += url_
    }
    else {
      numberOfDuplicateLinks += 1
    }
  }

  def act() {
    loop {
      react {
        case item : Map[String, String]       => pipeline ! item
        case (spider : Spider, url : String)  => crawlPage(spider, url)
        case _                                => require(false)
      }
    }
  }
  
  def printStats(print : String => Unit) {
    print("\nSet size               	= " + setOfLinksAlreadyVisited.size)
    print("\n# messages in mailbox 	= " + this.mailboxSize)
    print("\n# duplicate links ignored = " + numberOfDuplicateLinks)
  }
}

