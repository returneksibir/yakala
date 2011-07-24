package yakala.db

import yakala.logging.Logger
import java.net.URLEncoder
import org.jsoup.Jsoup

trait BookDB {
  def save(book : Book)
}

class GoogleAppEngineBookDB(logger : Logger) extends BookDB{

  val BOOK_SERVICE_ADDRESS = "http://rimbiskitapsever.appspot.com/book"
  //val BOOK_SERVICE_ADDRESS = "http://localhost:8080/book"

  def save(book : Book) {
    val urlParameters = "isbn=" + book.isbn + "&price=" + book.price + "&link=" + URLEncoder.encode(book.url, "UTF-8") + "&store=" + book.storeID;
    val url = BOOK_SERVICE_ADDRESS + "?" + urlParameters
    logger.debug("Connecting to " + url)
    Jsoup.connect(url).execute()
  }
}

class DummyBookDB(logger : Logger) extends BookDB{

  val BOOK_SERVICE_ADDRESS = "http://rimbiskitapsever.appspot.com/book"

  def save(book : Book) {
    val urlParameters = "isbn=" + book.isbn + "&price=" + book.price + "&link=" + URLEncoder.encode(book.url, "UTF-8") + "&store=" + book.storeID;
    val url = BOOK_SERVICE_ADDRESS + "?" + urlParameters
    logger.info("Connecting to " + url)
  }
}

class Book (price : String, isbn : String, url : String, storeID : String) {

  def price()   : String  = price
  def isbn()    : String  = isbn
  def url()     : String  = url
  def storeID() : String  = storeID

  def print(logger : Logger) {
    logger.info("ISBN   = " + isbn)
    logger.info("Fiyat  = " + price + " TL")
    logger.info("Url    = " + url)
    logger.info("Store  = " + storeID)
  }
}

