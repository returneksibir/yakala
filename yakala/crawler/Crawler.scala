package yakala.crawler

import yakala.logging.Logger
import yakala.pipelines._
import yakala.spiders._
import yakala.Settings
import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.mutable.Set
import collection.immutable.Map
import util.Random

class Crawler(logger : Logger, spider : Spider, pipeline : ItemPipeline) {
  
  private val random = new Random()

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

          case relativeURLPattern2(matchingStr)  => return spider.domainName + matchingStr

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

  def run(startURL : String) {
    val setOfLinksToBeVisited : Set[String]  = Set(startURL)
    val setOfLinksAlreadyVisited : Set[String] = Set()

    while (!setOfLinksToBeVisited.isEmpty) {
      setOfLinksToBeVisited foreach { url =>
      
        try {

          logger.info("Sayfa :" + url)
    
          val doc = Jsoup.connect(url).get()
  
          if (spider.isProductPage(url)) {
            val title     = doc.title();
            logger.info("------- " + title + " -------")
            try {
              val bookMap = spider.processItem(doc)
              val book = new Book(bookMap("price"), bookMap("isbn"), url, bookMap("STORE_ID"))
              book.print(logger)
              pipeline.processItem(book)
            } catch {
              case e => logger.info(e.getMessage())
            }
          }

          var linksOnPage = getLinks(doc)
          linksOnPage = linksOnPage.filter{ link => (link.startsWith(spider.domainName) || (!link.startsWith("http://") && !link.startsWith("javascript:")))}
          linksOnPage.foreach{ href => 
            val link = MakeUrl(url, href) 
            if (!setOfLinksAlreadyVisited.contains(link))
              setOfLinksToBeVisited += link
          }
    
        } catch {
          case e : java.net.SocketTimeoutException  => logger.debug("Exception :" + e.getMessage())
          case e : java.net.UnknownHostException    => logger.debug("Exception :" + e.getMessage())
          case e : java.io.IOException              => logger.debug("Exception :" + e.getMessage())
        }

        setOfLinksToBeVisited     -= url
        setOfLinksAlreadyVisited  += url
  
        logger.debug("Gezilen   sayfa sayısı : " + setOfLinksAlreadyVisited.size)
        logger.debug("Gezilecek sayfa sayısı : " + setOfLinksToBeVisited.size)

        sleepForAWhile()
      }
    }
  }
  def sleepForAWhile() {
    //Let's wait for a random time in the range between MIN_WAIT_TIME_IN_MS ~ MAX_WAIT_TIME_IN_MS ms

    val sleepTime = Settings.MIN_WAIT_TIME_IN_MS + Math.abs(random.nextInt()) % (Settings.MAX_WAIT_TIME_IN_MS - Settings.MIN_WAIT_TIME_IN_MS)

    Thread.sleep(sleepTime)
  }
}

