package yakala.spiders

import yakala.Settings
import yakala.logging._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.actors.Actor
import scala.actors.Actor._
import util.Random

trait Spider extends Actor {
  def domainName()  : String
  def startURL()    : String
  def processItem(doc : Document) : Map[String, String]
  def productPagePattern()  : util.matching.Regex
  def followRulePattern()   : util.matching.Regex
  
  private var numberOfExtractedLinks = 0
  private val random = new Random()
  private val logger : Logger = new ConsoleLogger()
  logger.setLogLevel(Logger.LOG_INFO)

  def isProductPage(pageUrl : String) : Boolean = {
    try {
      val PatternMatcher = productPagePattern;
      val PatternMatcher(matchStr) = pageUrl
      return true
    } catch {
      case e : MatchError => return false
    }
  }

  def getLinks(doc : Document) : collection.immutable.Set[String] = {
    var linksSet : collection.immutable.Set[String] = collection.immutable.Set()
    val links = doc.select("a[href]"); // a with href
    val iter = links.iterator()
    while(iter.hasNext()) {
      val link = iter.next()
      val href = link.attr("href")
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

      case relativeURLPattern2(matchingStr)  => return "http://www." + domainName + "/" + matchingStr

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
  
      val doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()
  
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
      linksOnPage = linksOnPage.filter{ link => 
        val link_       = link.toLowerCase()
        val domainName_ = domainName.toLowerCase()
        link_.startsWith("http://" + domainName_) ||
        link_.startsWith("http://www." + domainName_) ||
        (!link_.startsWith("http://") && !link_.startsWith("javascript:") && !link_.startsWith("mailto:"))
      }

      linksOnPage.foreach{ href => 
        val link = MakeUrl(url, href)
        val FOLLOW_RULE_PATTERN = followRulePattern
        link match {
          case FOLLOW_RULE_PATTERN(_) => 
            numberOfExtractedLinks += 1
            sender ! (this, link)
          case _ => logger.debug("The link does not match the follow rule, ignoring it")
        }
      }
      
    } catch {
      case e  => logger.debug("Exception :" + e.getMessage())
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

  def printStats(print : String => Unit) {
    print("\n" + domainName + "(" + numberOfExtractedLinks + ", " + mailboxSize + ")")
  }
}

