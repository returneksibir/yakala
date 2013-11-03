package yakala.console
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop

object Console extends App {
  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true
  new YakalaLoop().process(settings)
}

class YakalaLoop extends ILoop {
  override def prompt = "yakala> "
  override def createInterpreter() {
    intp = new YakalaInterpreter
  }

  class YakalaInterpreter extends ILoopInterpreter {
    def prevRequest: Option[Request] = Option(lastRequest)
    def lastValue: Option[AnyRef] = prevRequest flatMap (_.lineRep.callOpt("$result"))
  }
}


