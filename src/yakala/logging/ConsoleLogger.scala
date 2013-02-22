package yakala.logging

class ConsoleLogger extends Logger {
  protected def log(logMsg : String) = println(logMsg)
}

