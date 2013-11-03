import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop

object Main extends App {
  type ArgumentMap = Map[Symbol, Any]

  def parseArgs(map: ArgumentMap, list: List[String]): ArgumentMap = {
    list match {
      case Nil => map
      case "console" :: tail =>
        val settings = new Settings
        settings.usejavacp.value = true
        settings.deprecation.value = true
        new YakalaLoop().process(settings)
        parseArgs(map ++ Map('shell -> true), tail)
      case arg :: tail =>
        println("Invalid argument: " + arg)
        map
    }
  }

  val argm = parseArgs(Map(), args.toList)
}

class YakalaLoop extends ILoop {
  override def prompt = "yakala-admin> "
  override def createInterpreter() {
    intp = new YakalaInterpreter
  }

  class YakalaInterpreter extends ILoopInterpreter {
    def prevRequest: Option[Request] = Option(lastRequest)
    def lastValue: Option[AnyRef] = prevRequest flatMap (_.lineRep.callOpt("$result"))
  }
}
