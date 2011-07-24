package yakala.pipelines

import yakala.logging.Logger
import java.net.URLEncoder

class DummyBookDB(logger : Logger) extends ItemPipeline {

  val BOOK_SERVICE_ADDRESS = "http://rimbiskitapsever.appspot.com/book"

  def processItem(book : Book) {
    val urlParameters = "isbn=" + book.isbn + "&price=" + book.price + "&link=" + URLEncoder.encode(book.url, "UTF-8") + "&store=" + book.storeID;
    val url = BOOK_SERVICE_ADDRESS + "?" + urlParameters
    logger.info("Connecting to " + url)
  }
}
