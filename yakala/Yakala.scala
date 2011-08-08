package yakala

import yakala.pipelines._
import yakala.spiders._
import yakala.crawler._
import yakala.logging._
import scala.actors.Actor

object Yakala {
  def main(args : Array[String]) {

    val url = args(0)
    val logger    : Logger       = new ConsoleLogger()
    logger.setLogLevel(Logger.LOG_DEBUG)

    val spiders = List( new PandoraSpider(logger), new ImgeSpider(logger) )
    val pipeline: ItemPipeline = new GoogleAppEngineBookDB(logger)
    val crawler = new Crawler(logger)

    val spiderMap = spiders.map(spider => (spider.domainName, spider.startURL)).toMap
    val argList = args.toList
    val runList = argList.intersect(spiderMap.keys.toList)
    runList.foreach(crawler ! spiderMap(_))
  }
}
