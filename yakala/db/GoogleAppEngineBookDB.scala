package yakala.db

import yakala.logging.Logger
import java.net.URLEncoder
import org.jsoup.Jsoup

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

