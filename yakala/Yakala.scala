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

    val spiders = List( new PandoraSpider(logger), new ImgeSpider(logger) )

    val pipeline  : ItemPipeline = new GoogleAppEngineBookDB(logger)

    val crawler = new Crawler(logger, pipeline)
    crawler.start

    args.foreach{ domainName => 
      spiders.foreach{ spider =>
        if (domainName == spider.domainName) {
          spider.start
          crawler ! (spider, spider.startURL)
        }
      }
    }
  }

}

