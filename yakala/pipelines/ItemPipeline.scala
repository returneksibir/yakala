package yakala.pipelines

import yakala.logging.Logger

trait ItemPipeline {
  def processItem(book : Book)
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

