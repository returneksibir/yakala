import scala.tools.nsc.Settings

import yakala_admin.console.YakalaConsole

object Main extends App {
  type ArgumentMap = Map[Symbol, Any]

  def parseArgs(map: ArgumentMap, list: List[String]): ArgumentMap = {
    list match {
      case Nil => map
      case "console" :: tail =>
        val settings = new Settings
        settings.usejavacp.value = true
        settings.deprecation.value = true
        new YakalaConsole().process(settings)
        parseArgs(map ++ Map('shell -> true), tail)
      case "help" :: tail =>
        val msg = s"""
yakala-admin supported commands:

        console         : drops you into console.
        help            : this message.

"""
        print(msg)
        map
      case arg :: tail =>
        println("Invalid argument: " + arg)
        map
    }
  }

  val argm = parseArgs(Map(), args.toList)
}
