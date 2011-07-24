package yakala

import yakala.pipelines._
import yakala.spiders._
import yakala.crawler._
import yakala.logging._

object Yakala {
  
  def main(args : Array[String]) {

    val url = args(0)

    val logger    : Logger       = new ConsoleLogger()
    logger.setLogLevel(Logger.LOG_INFO)

    val pipeline  : ItemPipeline = new DummyBookDB(logger)
    val spider    : Spider       = new PandoraSpider(logger)
    spider.start

    val crawler = new Crawler(logger, spider, pipeline)
    crawler.start
    crawler ! url
  }

}

