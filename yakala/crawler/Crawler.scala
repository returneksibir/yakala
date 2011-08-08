package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import yakala.registery._
import collection.mutable.Set
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger) extends Actor {
  private var setOfLinksAlreadyVisited : Set[String] = Set()
  private val actorSelf = this.start

  def act() {
    loop {
      react {
	case url: String => {
	  if (!setOfLinksAlreadyVisited.contains(url)) {
	    setOfLinksAlreadyVisited  += url
	    val consumers = Registery.filter(url)
	    logger.debug("[Crawler] Pushing url : " + url + " (pushed url count : " + setOfLinksAlreadyVisited.size + ")")
	    consumers.foreach( _ ! (actorSelf, url) )
	  }
	}

	case vl: Any => {
	  val consumers = Registery.filter(vl)
	  logger.debug("[Crawler] Consumer list: '" + consumers + "', relevant data : " + vl)
	  consumers.foreach( _ ! (actorSelf, vl) )
	}
      }
    }
  }
}
