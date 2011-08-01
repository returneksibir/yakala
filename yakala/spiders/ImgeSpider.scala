package yakala.spiders

import yakala.logging.Logger
import org.jsoup.nodes.Document


class ImgeSpider(logger : Logger) extends Spider {
  private val DOMAIN_NAME         = "imge.com.tr"
  private val BOOK_PRICE_PATH     = "span.productSpecialPrice"
  private val BOOK_ISBN_PATH      = "table#ana_alan"
  private val STORE_ID            = 4
  private val BOOK_PAGE_PATTERN   = "http://www.imge.com.tr/product_info.php?products_id="

  def isProductPage(pageUrl : String) : Boolean = { pageUrl.startsWith(BOOK_PAGE_PATTERN) }
  def domainName() : String = DOMAIN_NAME
  
  def processItem(doc : Document) : Map[String, String] = {
    val title    = doc.title();
    logger.info("------- " + title + " -------")
    try {
      val bookPrice = doc.select(BOOK_PRICE_PATH).first().text().trim().replace(",", ".")
      val PricePattern = """.* (\S+)TL""".r
      val PricePattern(price) = bookPrice
      var bookIsbn      = doc.select(BOOK_ISBN_PATH).first().text().trim().replace("-", "")
      val IsbnPattern = """.* ISBN: (\S+) .*""".r
      var IsbnPattern(isbn) = bookIsbn
      val len = isbn.length()
      isbn = if (len < 10) isbn else isbn.substring(len-10, len-1)
      Map("price" ->  price, "isbn"  ->  isbn, "storeID" ->  STORE_ID.toString())
    } catch {
      case e : NullPointerException => throw new Exception("Düzgün biçimli kitap bilgisi bulunamadı.")
      case e : MatchError           => throw new Exception("Price information is not in TL")
    }
  }
}

