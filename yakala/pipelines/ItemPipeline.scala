package yakala.pipelines

import yakala.logging.Logger
import yakala.registery._
import yakala.tools.Matcher
import scala.actors.Actor

trait ItemPipeline extends Actor {
  this.start

  def processItem(item : Map[String, String])

  def check(data: Any) = {
    val MapStringString = new Matcher[Map[String, String]]
    data match {
      case MapStringString(vl) => true
      case _ => false
    }
  }

  override def start = {
    val actorSelf = super.start
    Registery.register(actorSelf, check _)
    actorSelf
  }

  def act() {
    val MapStringString = new Matcher[Map[String, String]]
    loop {
      react {
	case (caller: Actor, item: Any) => {
	  item match {
	    case MapStringString(vl) => 
	      processItem(vl)
	    case _ => require(false)
	  }
	}
	case _ =>
	  require(false)
      }
    }
  }
}

