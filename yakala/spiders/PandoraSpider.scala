package yakala.spiders

import yakala.logging.Logger
import org.jsoup.nodes.Document


class PandoraSpider(logger : Logger) extends Spider {
  private val STORE_URL           = "http://www.pandora.com.tr/"
  private val BOOK_PRICE_PATH     = "span.fiyat"
  private val BOOK_ISBN_PATH      = "span#ContentPlaceHolderMainOrta_LabelIsbn"
  private val STORE_ID            = 4
  private val BOOK_PAGE_PATTERN   = "http://www.pandora.com.tr/urun/"

  def isProductPage(pageUrl : String) : Boolean = { pageUrl.startsWith(BOOK_PAGE_PATTERN) }
  def domainName() : String = STORE_URL
  
  def processItem(doc : Document) : Map[String, String] = {
    val title    = doc.title();
    logger.info("------- " + title + " -------")
    try {
      val bookPrice = doc.select(BOOK_PRICE_PATH).first().text().trim().replace(",", ".")
      var isbn      = doc.select(BOOK_ISBN_PATH).first().text().trim().replace("-", "")
      val PricePattern = """(\S+) TL""".r
      val PricePattern(price) = bookPrice
      val len = isbn.length()
      isbn = if (len < 10) isbn else isbn.substring(len-10, len-1)
      Map("price" ->  price, "isbn"  ->  isbn, "storeID" ->  STORE_ID.toString())
    } catch {
      case e : NullPointerException => throw new Exception("Düzgün biçimli kitap bilgisi bulunamadı.")
      case e : MatchError           => throw new Exception("Price information is not in TL")
    }
  }
}

