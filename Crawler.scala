import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.mutable.Set
import util.Random
import java.net.URLEncoder
import yakala._

object Crawler {
  
  val SITE_URL            = "http://www.pandora.com.tr/"
  val STORE_ID            = 4
  val BOOK_NAME_PATH      = "div.kitaptitle2 > h1"
  val BOOK_PRICE_PATH     = "span.fiyat"
  val BOOK_ISBN_PATH      = "span#ContentPlaceHolderMainOrta_LabelIsbn"
  val BOOK_PAGE_PATTERN   = "http://www.pandora.com.tr/urun/"
  val MIN_WAIT_TIME_IN_MS = 100
  val MAX_WAIT_TIME_IN_MS = 500
  val BOOK_SERVICE_ADDRESS = "http://rimbiskitapsever.appspot.com/book"
  //val BOOK_SERVICE_ADDRESS = "http://localhost:8080/book"
  val logger = new ConsoleLogger()

  def selectAndPrintProductInfo(doc : Document, pageUrl : String) {

    val title     = doc.title();
    val bookName  = doc.select(BOOK_NAME_PATH).first()
    val bookPrice = doc.select(BOOK_PRICE_PATH).first()
    val isbn      = doc.select(BOOK_ISBN_PATH).first()

    val strBookPrice = try {
      val Price = """(\S+) TL""".r
      val Price(price) = bookPrice.text().trim().replace(",", ".")
      price
    } catch {
      case e : NullPointerException => logger.info("Price information is not available"); return
      case e : MatchError           => logger.info("Price information is not in TL"); return
    }

    try {
      var strIsbn = isbn.text().trim().replace("-", "")
      val len = strIsbn.length()
      strIsbn = if (len < 10) strIsbn else strIsbn.substring(len-10, len-1)
      logger.info("------- " + title + " -------")
      logger.info("Kitap = " + bookName.text())
      logger.info("ISBN  = " + strIsbn)
      logger.info("Fiyat = " + strBookPrice + " TL")
      val urlParameters = "isbn=" + strIsbn + "&price=" + strBookPrice + "&link=" + URLEncoder.encode(pageUrl, "UTF-8") + "&store=" + STORE_ID;
      val url = BOOK_SERVICE_ADDRESS + "?" + urlParameters
      logger.debug("Connecting to " + url)
      Jsoup.connect(url).execute()
    } catch {
      case e : NullPointerException => logger.info("Düzgün biçimli kitap bilgisi bulunamadı.")
    }

  }

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

          case relativeURLPattern2(matchingStr)  => return SITE_URL + matchingStr

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

  def isProductPage(pageUrl : String) : Boolean = { pageUrl.startsWith(BOOK_PAGE_PATTERN) }

  def main(args : Array[String]) {
    val url = args(0)

    logger.setLogLevel(Logger.LOG_INFO)

    val setOfLinksToBeVisited : Set[String]  = Set(url)
    val setOfLinksAlreadyVisited : Set[String] = Set()

    val random = new Random()

    while (!setOfLinksToBeVisited.isEmpty) {
      setOfLinksToBeVisited foreach { url =>
      
        try {

          logger.info("Sayfa :" + url)
    
          val doc = Jsoup.connect(url).get()
  
          if (isProductPage(url))
            selectAndPrintProductInfo(doc, url)
          
          var linksOnPage = getLinks(doc)
          linksOnPage = linksOnPage.filter{ link => (link.startsWith(SITE_URL) || (!link.startsWith("http://") && !link.startsWith("javascript:")))}
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

       //Let's wait for a random time in the range between MIN_WAIT_TIME_IN_MS ~ MAX_WAIT_TIME_IN_MS ms
  
        val sleepTime = MIN_WAIT_TIME_IN_MS + Math.abs(random.nextInt()) % (MAX_WAIT_TIME_IN_MS - MIN_WAIT_TIME_IN_MS)
  
        Thread.sleep(sleepTime)
      }
    }
  }
}

