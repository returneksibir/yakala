package yakala.pipelines

import yakala.logging.Logger
import scala.actors.Actor
import scala.actors.Actor._


abstract class ItemPipeline extends Actor {
  def processItem(item : Map[String, String])
  
  def act() {
    loop {
      react {
        case item : Map[String, String]       => processItem(item)
        case _                                => require(false)
      }
    }
  }
}

