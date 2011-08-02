import scala.actors.Actor
import scala.actors.Actor._

object ActorRegistry {
  private var objs: Map[String, (Any => Boolean)] = Map()
  def register(K: String, V: (Any => Boolean)) {
    objs += (K -> V)
  }

  def entries = objs
}

class A extends Actor {
  override def start = {
    ActorRegistry.register("deneme", check _)
    super.start
  }

  def act() {
    loop {
      react {
	case msg: String =>
	  println("int has come " + msg)
      }
    }
  }

  def check(data: Any): Boolean = {
    data match {
      case v: Int => println("this is int so im ok with it");true
      case _ => println("no match");false
    }
  }
}

class B extends Actor {
  override def start = {
    ActorRegistry.register("hop", check _)
    super.start
  }

  def act() {
    loop {
      react {
	case num: Int =>
	  println("int has come " + num)
      }
    }
  }

  def check(data: Any): Boolean = {
    data match {
      case v: String => println("it is string so nice"); true
      case _ => println("no match, no good"); false
    }
  }
}

object ActorDesignEx extends App {
  /* 
   * Theory of Operation:
   * Every class register itself when starts to run,
   * on an actor (when start method is called).
   * Actor reference as key value should be saved
   * to the registery.
   * Right now meaninglis String's are being saved.
   * As pair of key, a checker function by provided
   * by a class should be saved. This function's
   * prototype is (Any => Boolean) right now.
   * The main loop tests some data against
   * checker functions and prints the list.
   */

  val a = new A
  val b = new B
  a.start
  b.start

  val data_list = List(10, "olur mu ya", List(1, 2, 3, 4))
  val entries = ActorRegistry.entries
  /* Iterate each elemen in data_list and test using tester function. */
  data_list.foreach {data => println("testing data " + data + "\n" + entries.filter {case (k, v) => v(data)})}
}
