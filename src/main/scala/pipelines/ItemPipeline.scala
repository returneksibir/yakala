package yakala.pipelines

import yakala.logging.Logger
import scala.actors.Actor
import scala.actors.Actor._


abstract class ItemPipeline(logger : Logger) extends Actor {
  def processItem(item : Map[String, String])
  

  var numberOfProcessedItems : Long = 0;
  var numberOfExceptions : Long = 0;

  def act() {
    loop {
      react {
        case item : Map[String, String]       =>
          numberOfProcessedItems += 1
          try {
            processItem(item)
          } catch {
            case e  =>
            numberOfExceptions += 1
            logger.debug("Exception :" + e.getMessage())
          }

        case _                                => require(false)
      }
    }
  }

  def printStats(print : String => Unit) {
    print("\n# messages in mailbox = " + this.mailboxSize)
    print("\n# processed items = " + numberOfProcessedItems)
    print("\n# exceptions = " + numberOfExceptions)
  }

}

