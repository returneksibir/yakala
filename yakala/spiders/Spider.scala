package yakala.spiders

import yakala.logging._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.actors.Actor
import scala.actors.Actor._
import util.Random

trait Spider extends Actor {
  def domainName() : String
  def processItem(doc : Document) : Map[String, String]
  def isProductPage(pageUrl : String) : Boolean

  private val random = new Random()
  private val logger : Logger = new ConsoleLogger()
  logger.setLogLevel(Logger.LOG_INFO)

  def getLinks(doc : Document) : collection.immutable.Set[String] = {
    var linksSet : collection.immutable.Set[String] = collection.immutable.Set()
    val links = doc.select("a[href]"); // a with href
    val iter = links.iterator()
    while(iter.hasNext()) {
      val link = iter.next()
      val href = link.attr("href").toLowerCase()
      linksSet += href
    }

    return linksSet
  }

  def MakeUrl(pageUrl : String, href : String) : String = {
    val fullURLPattern = """http://(.+)""".r
    val relativeURLPattern2 = """^/(.+)""".r
    val relativeURLPattern3 = """^./(.+)""".r
    val relativeURLPattern4 = """^../(.+)""".r
    val relativeURLPattern5 = """(.+)""".r
    val fragmentPattern     = """(.*)\#.*""".r

    logger.debug("href         : " + href)
    logger.debug("pageUrl      : " + pageUrl)

    href match {
      case fullURLPattern(matchingStr)       => return href

      case relativeURLPattern2(matchingStr)  => return domainName + matchingStr

      case relativeURLPattern3(matchingStr)  => 
        logger.debug("matchingStr  : " + matchingStr)
        return MakeUrl( matchingStr, pageUrl)

      case relativeURLPattern4(matchingStr)  => 
        logger.debug("matchingStr  : " + matchingStr)
        var index = pageUrl.lastIndexOf("/")
        return MakeUrl( matchingStr, pageUrl.substring(0, index))

      case fragmentPattern(matchingStr)  => 
        logger.debug("matchingStr  : " + matchingStr)
        var tmpStr = matchingStr.trim()
        return if (!tmpStr.isEmpty) MakeUrl(tmpStr, pageUrl) else pageUrl

      case relativeURLPattern5(matchingStr)  => 
        logger.debug("matchingStr  : " + matchingStr)
        val index = pageUrl.lastIndexOf("/")
        return pageUrl.substring(0, index) + "/" + matchingStr
    }
  }

  def visitPage(url : String) {    
    try {

      logger.info("Sayfa :" + url)
  
      val doc = Jsoup.connect(url).get()
  
      if (isProductPage(url)) {
        try {
          var bookMap  = processItem(doc)
          bookMap += "url" -> url
          sender ! bookMap
        } catch {
          case e => logger.info(e.getMessage())
        }
      }

      var linksOnPage = getLinks(doc)
      linksOnPage = linksOnPage.filter{ link => (link.startsWith(domainName) || (!link.startsWith("http://") && !link.startsWith("javascript:")))}
      linksOnPage.foreach{ href => 
        val link = MakeUrl(url, href) 
        sender ! link
      }
  
    } catch {
      case e : java.net.SocketTimeoutException  => logger.debug("Exception :" + e.getMessage())
      case e : java.net.UnknownHostException    => logger.debug("Exception :" + e.getMessage())
      case e : java.io.IOException              => logger.debug("Exception :" + e.getMessage())
    }
  }

  def sleepForAWhile() {
    //Let's wait for a random time in the range between MIN_WAIT_TIME_IN_MS ~ MAX_WAIT_TIME_IN_MS ms

    val sleepTime = Settings.MIN_WAIT_TIME_IN_MS + Math.abs(random.nextInt()) % (Settings.MAX_WAIT_TIME_IN_MS - Settings.MIN_WAIT_TIME_IN_MS)

    Thread.sleep(sleepTime)
  }

  def act() {
    loop {
      react {
        case url: String =>
          visitPage(url)
          sleepForAWhile()
        case _ =>
          require(false)
      }
    }
  }
}

