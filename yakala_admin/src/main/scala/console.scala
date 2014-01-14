package yakala_admin.console

import scala.tools.nsc.interpreter.ILoop

class YakalaConsole extends ILoop {
  override def prompt = "yakala-admin> "
  override def createInterpreter() {
    intp = new YakalaInterpreter
  }

  class YakalaInterpreter extends ILoopInterpreter {
    def prevRequest: Option[Request] = Option(lastRequest)
    def lastValue: Option[AnyRef] = prevRequest flatMap (_.lineRep.callOpt("$result"))
  }
}
