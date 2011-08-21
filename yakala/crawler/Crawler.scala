package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines.ItemPipeline
import yakala.spiders.Spider
import yakala.registery._
import yakala.refdb._
import collection.immutable.Map
import scala.actors.Actor
import scala.actors.Actor._


class Crawler(logger : Logger) extends Actor {
  private val linkDb = new refDbH2("CrawledLinks")
  private val thisActor = this.start
  private var counter = 0

  def act() {
    loop {
      react {
	case url: String => {
	  println("Crawler mailboxSize : " + mailboxSize + ", url : " + url)
	  val urlId = url.hashCode
	  if (!linkDb.hasRef(urlId)) {
	    linkDb.addRef(new Ref(urlId, url))
	    val consumers = Registery.filter(url)
	    counter += 1
	    logger.debug("[Crawler] Pushing url : " + url + " (pushed url count : " + counter + ")")
	    consumers.foreach( _ ! (thisActor, url) )
	  }
	  // if (!setOfLinksAlreadyVisited.contains(url)) {
	  //   setOfLinksAlreadyVisited  += url
	  //   val consumers = Registery.filter(url)
	  //   logger.debug("[Crawler] Pushing url : " + url + " (pushed url count : " + setOfLinksAlreadyVisited.size + ")")
	  //   consumers.foreach( _ ! (thisActor, url) )
	  // }
	}

	case vl: Any => {
	  println("Crawler mailboxSize : " + mailboxSize + ", data : " + vl)
	  val consumers = Registery.filter(vl)
	  logger.debug("[Crawler] Consumer list: '" + consumers + "', relevant data : " + vl)
	  consumers.foreach( _ ! (thisActor, vl) )
	}
      }
    }
  }
}
