package yakala.pipelines

import yakala.logging.Logger
import yakala.registery._
import scala.actors.Actor

trait ItemPipeline extends Actor {
  this.start

  def processItem(item : Map[String, String])

  def check(data: Any) = {
    data match {
      case vl: Map[String, String] => true
      case _ => false
    }
  }

  override def start = {
    val actorId = super.start
    Registery.register(actorId, check _)
    actorId
  }

  def act() {
    loop {
      react {
	case (caller: Actor, item: Map[String, String]) =>
	  processItem(item)
	case _ =>
	  require(false)
      }
    }
  }
}

