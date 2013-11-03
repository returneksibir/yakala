package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import yakala.utils.SetTrait
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger,
		pipelines : List[ItemPipeline],
		setOfLinksAlreadyVisited : SetTrait
		) extends Actor {
  
  private var numberOfDuplicateLinks = 0

  def crawlPage(spider : Spider, url : String)  {
    val url_ = url.toLowerCase()
    if (!setOfLinksAlreadyVisited.contains(url_)) {
      spider ! url
      setOfLinksAlreadyVisited.add(url_)
    }
    else {
      numberOfDuplicateLinks += 1
    }
  }

  def act() {
    loop {
      react {
        case item : Map[String, String]       =>
          pipelines.foreach {
            pipeline => pipeline ! item
          }
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

