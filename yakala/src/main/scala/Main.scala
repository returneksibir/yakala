package yakala

object Main {
  def main(args: Array[String]) {
    val spiders =  System.getProperty("user.dir") + "/spiders"
    val spider = java.io.File(spiders).listFiles.filter(s).filter(_.getName.endsWith(".scala"))
    println("spiders are: " + spider)
  }
}
