import scala.tools.nsc.Settings
import yakala_admin.console.YakalaConsole
import yakala_admin.utils.createProject

object Main extends App {
  type ArgumentMap = Map[Symbol, Any]

  def parseArgs(map: ArgumentMap, list: List[String]) = {
    list match {
      case Nil => map
      case "console" :: tail =>
        val settings = new Settings
        settings.usejavacp.value = true
        settings.deprecation.value = true
        new YakalaConsole().process(settings)
      case "help" :: tail =>
        val msg = s"""
yakala-admin supported commands:

        console                 : drops you into console.
        create <project>        : creates a new project.
        help                    : this message.

"""
        print(msg)
      case "create" :: tail => tail match {
        case (project:String) :: _ =>
          println("project name is: " + project)
        case _ =>
          println("missing projectname")
      }
      case arg :: tail =>
        println("Invalid argument: " + arg)
    }
  }

  val argm = parseArgs(Map(), args.toList)
}
