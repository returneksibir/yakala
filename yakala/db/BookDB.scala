package yakala.db

import yakala.logging.Logger

trait BookDB {
  def save(book : Book)
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

