package yakala.logging

object Logger {
  val LOG_DEBUG   = 0
  val LOG_INFO    = 1
  val LOG_WARNING = 2
  val LOG_ERROR   = 3
}

trait Logger {
  private var logLevel = Logger.LOG_ERROR

  protected def log(logMsg : String)
  private def log(logLevel : Int, logMsg : String) { if (logLevel >= this.logLevel) log(logMsg) }

  def setLogLevel(logLevel : Int) { this.logLevel = logLevel }
  def debug(msg : String)   { log(Logger.LOG_DEBUG, msg)   }
  def info(msg : String)    { log(Logger.LOG_INFO, msg)    }
  def warning(msg : String) { log(Logger.LOG_WARNING, msg) }
  def error(msg : String)   { log(Logger.LOG_ERROR, msg)   }
}

