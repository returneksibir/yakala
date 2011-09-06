package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import yakala.registery._
import collection.mutable.Set
import scala.actors.Actor
import scala.actors.Actor._

sealed class CrawlerLinkDb {
  var db: List[Set[String]] = List(Set.empty)
  val limit = 1000000
  
  def contains(v: String): Boolean = {
    val currDb: Set[String] = db.head
    currDb.contains(v)
  }

  def add(v: String) {
    db.foreach{ currDb =>
      if (currDb.contains(v)) {
	true
      }
    }
    if (db.head.size > limit) {
      val newHead: Set[String] = Set.empty
      db = newHead :: db
    }
    db.head.add(v)
  }

  def += = this.add _

  def size = {
    val sizeList = for (list <- db) yield list.size
    sizeList.sum
  }
}

class Crawler(logger : Logger) extends Actor {
//  private val setOfLinksAlreadyVisited : Set[String] = Set()
  private val setOfLinksAlreadyVisited = new CrawlerLinkDb
  private val thisActor = this.start

  def act() {
    loop {
      react {
	case url: String => {
	  if (!setOfLinksAlreadyVisited.contains(url)) {
	    setOfLinksAlreadyVisited  += url
	    val consumers = Registery.filter(url)
	    logger.debug("[Crawler] Pushing url : " + url + " (pushed url count : " + setOfLinksAlreadyVisited.size + ")")
	    consumers.foreach( _ ! (thisActor, url) )
	  }
	}

	case vl: Any => {
	  val consumers = Registery.filter(vl)
	  logger.debug("[Crawler] Consumer list: '" + consumers + "', relevant data : " + vl)
	  consumers.foreach( _ ! (thisActor, vl) )
	}
      }
    }
  }
}
