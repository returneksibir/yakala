package yakala

import yakala.db._
import yakala.spiders._
import yakala.crawler._

object Yakala {
  
  def main(args : Array[String]) {

    val url = args(0)

    val logger : Logger     = new ConsoleLogger()
    val bookDB : BookDB     = new DummyBookDB(logger)
    val spider : Spider     = new PandoraSpider()

    logger.setLogLevel(Logger.LOG_DEBUG)

    new Crawler(logger, spider, bookDB).run(url)
  }

}

