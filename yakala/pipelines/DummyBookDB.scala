package yakala.pipelines

import yakala.logging.Logger
import java.net.URLEncoder

class DummyBookDB(logger : Logger) extends ItemPipeline {

  val BOOK_SERVICE_ADDRESS = "http://rimbiskitapsever.appspot.com/book"

  def processItem(item : Map[String, String]) {
    item.foreach{case (key, value) => logger.info(key + "\t:" + value)}
    val urlParameters = "isbn=" + item("isbn") + "&price=" + item("price") + "&link=" + URLEncoder.encode(item("url"), "UTF-8") + "&store=" + item("storeID");
    val url = BOOK_SERVICE_ADDRESS + "?" + urlParameters
    logger.info("Connecting to " + url)
  }
}

