package yakala

import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.mutable.Set
import util.Random
import yakala.db._

class Book (price : String, isbn : String, url : String, storeID : Int) {

  def price()   : String  = price
  def isbn()    : String  = isbn
  def url()     : String  = url
  def storeID() : Int     = storeID

  def print(logger : Logger) {
    logger.info("ISBN   = " + isbn)
    logger.info("Fiyat  = " + price + " TL")
    logger.info("Url    = " + url)
    logger.info("Store  = " + storeID)
  }
}

object Crawler {
  
  val STORE_URL           = "http://www.pandora.com.tr/"
  val STORE_ID            = 4
  val BOOK_NAME_PATH      = "div.kitaptitle2 > h1"
  val BOOK_PRICE_PATH     = "span.fiyat"
  val BOOK_ISBN_PATH      = "span#ContentPlaceHolderMainOrta_LabelIsbn"
  val BOOK_PAGE_PATTERN   = "http://www.pandora.com.tr/urun/"
  val MIN_WAIT_TIME_IN_MS = 100
  val MAX_WAIT_TIME_IN_MS = 500

  val logger : Logger     = new ConsoleLogger()
  val bookDB : BookDB     = new DummyBookDB(logger)

  def getBook(doc : Document, pageUrl : String) : Book  = {

    try {
      val bookPrice = doc.select(BOOK_PRICE_PATH).first().text().trim().replace(",", ".")
      var isbn      = doc.select(BOOK_ISBN_PATH).first().text().trim().replace("-", "")
      val PricePattern = """(\S+) TL""".r
      val PricePattern(price) = bookPrice
      val len = isbn.length()
      isbn = if (len < 10) isbn else isbn.substring(len-10, len-1)
      new Book(price, isbn, pageUrl, STORE_ID)
    } catch {
      case e : NullPointerException => throw new Exception("Düzgün biçimli kitap bilgisi bulunamadı.")
      case e : MatchError           => throw new Exception("Price information is not in TL")
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

          case relativeURLPattern2(matchingStr)  => return STORE_URL + matchingStr

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
  
          if (isProductPage(url)) {
            val title     = doc.title();
            logger.info("------- " + title + " -------")
            try {
              val book = getBook(doc, url)
              book.print(logger)
              bookDB.save(book)
            } catch {
              case e => logger.info(e.getMessage())
            }
          }

          var linksOnPage = getLinks(doc)
          linksOnPage = linksOnPage.filter{ link => (link.startsWith(STORE_URL) || (!link.startsWith("http://") && !link.startsWith("javascript:")))}
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

