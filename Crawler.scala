import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.mutable.Set
import util.Random

object Crawler {
  
  val SITE_URL            = "http://www.pandora.com.tr/"
  val BOOK_NAME_PATH      = "div.kitaptitle2 > h1"
  val BOOK_PRICE_PATH     = "span.fiyat"
  val BOOK_ISBN_PATH      = "span#ContentPlaceHolderMainOrta_LabelIsbn"
  val BOOK_PAGE_PATTERN   = "http://www.pandora.com.tr/urun/"
  val MIN_WAIT_TIME_IN_MS = 100
  val MAX_WAIT_TIME_IN_MS = 500

  def selectAndPrintProductInfo(doc : Document) {

    val title     = doc.title();
    val bookName  = doc.select(BOOK_NAME_PATH).first()
    val bookPrice = doc.select(BOOK_PRICE_PATH).first()
    val isbn      = doc.select(BOOK_ISBN_PATH).first()

    val txtBookPrice = try {
      val Price = """(\S+) TL""".r
      val Price(price) = bookPrice.text()
      price
    } catch {
      case e : NullPointerException => println("Price information is not available"); return
      case e : MatchError           => println("Price information is not in TL"); return
    }

    try {
      println("------- " + title + " -------")
      println("Kitap = " + bookName.text())
      println("ISBN  = " + isbn.text())
      println("Fiyat = " + txtBookPrice + " TL")
    } catch {
      case e : NullPointerException => println("Düzgün biçimli kitap bilgisi bulunamadı.")
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

  def fixAndGetLink(url : String, pageUrl : String) : String = {
        var href = url
        val fullURLPattern = """http://(.+)""".r
        val relativeURLPattern2 = """^/(.+)""".r
        val relativeURLPattern3 = """^./(.+)""".r
        val relativeURLPattern4 = """^../(.+)""".r
        val relativeURLPattern5 = """(.+)""".r
        href match {
          case fullURLPattern(matchingStr)       => return href

          case relativeURLPattern2(matchingStr)  => return SITE_URL + matchingStr

          case relativeURLPattern3(matchingStr)  => 
            println("href         : " + href)
            println("matchingStr  : " + matchingStr)
            println("pageUrl      : " + pageUrl)
            href = fixAndGetLink( matchingStr, pageUrl)
            println("Rel3 pattern match : " + href)
            return href

          case relativeURLPattern4(matchingStr)  => 
            println("href         : " + href)
            println("matchingStr  : " + matchingStr)
            println("pageUrl      : " + pageUrl)
            var index = pageUrl.lastIndexOf("/")
            href = fixAndGetLink( matchingStr, pageUrl.substring(0, index))
            println("########## Rel4 pattern match : " + href)
            return href

          case relativeURLPattern5(matchingStr)  => 
            println("href         : " + href)
            println("matchingStr  : " + matchingStr)
            println("pageUrl      : " + pageUrl)
            val index = pageUrl.lastIndexOf("/")
            href = pageUrl.substring(0, index) + "/" + matchingStr
            println("########## Rel5 pattern match : " + href)
            return href
        }
  }

  def main(args : Array[String]) {
    val url = args(0)

    val setOfLinksToBeVisited : Set[String]  = Set(url)
    val setOfLinksAlreadyVisited : Set[String] = Set()

    val random = new Random()

    while (!setOfLinksToBeVisited.isEmpty) {
      setOfLinksToBeVisited foreach { url =>
      
        try {

          println("Sayfa :" + url)
    
          val doc = Jsoup.connect(url).get()
  
          if (url.startsWith(BOOK_PAGE_PATTERN))
            selectAndPrintProductInfo(doc)
          
          var linksOnPage = getLinks(doc)
          linksOnPage = linksOnPage.filter{ link => (link.startsWith(SITE_URL) || (!link.startsWith("http://") && !link.startsWith("javascript:")))}
          linksOnPage.foreach{ href => 
            val link = fixAndGetLink(href, url) 
            if (!setOfLinksAlreadyVisited.contains(link))
              setOfLinksToBeVisited += link
          }
    
          setOfLinksToBeVisited     -= url
          setOfLinksAlreadyVisited  += url
    
          println("Gezilen   sayfa sayısı : " + setOfLinksAlreadyVisited.size)
          println("Gezilecek sayfa sayısı : " + setOfLinksToBeVisited.size)

        } catch {
          case e : java.net.SocketTimeoutException => println(e.getMessage())
          case e : java.net.UnknownHostException => println(e.getMessage())
        }

        //Let's wait for a random time in the range between MIN_WAIT_TIME_IN_MS ~ MAX_WAIT_TIME_IN_MS ms
  
        val sleepTime = MIN_WAIT_TIME_IN_MS + Math.abs(random.nextInt()) % (MAX_WAIT_TIME_IN_MS - MIN_WAIT_TIME_IN_MS)
  
        Thread.sleep(sleepTime)
      }
    }
  }
}

