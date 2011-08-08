package yakala.pipelines

import yakala.logging.Logger
import yakala.registery._
import yakala.tools.Matcher
import scala.actors.Actor

trait ItemPipeline extends Actor {
  this.start

  def processItem(item : Map[String, String])

  def check(data: Any) = {
    /*
     * Using asInstanceOf is very very ugly.
     * It is because of Any type of parameter.
     * See comment on registery object.
     */
    try {
      val MapStringString = new Matcher[Map[String, String]]
      data.asInstanceOf[Map[String, String]] match {
	case MapStringString(vl) => true
	case _ => false
      }
    } catch {
      case e: ClassCastException => false
    }
  }

  override def start = {
    val thisActor = super.start
    Registery.register(thisActor, check _)
    thisActor
  }

  def act() {
    val MapStringString = new Matcher[Map[String, String]]
    loop {
      react {
	case (caller: Actor, item: Any) => {
	  item.asInstanceOf[Map[String, String]] match {
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

