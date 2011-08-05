import scala.actors.Actor
import scala.actors.Actor._
import scala.reflect.Manifest

object ActorRegistry {
  private var objs: Map[Actor, (Any => Boolean)] = Map()
  def register(K: Actor, V: (Any => Boolean)) {
    objs += (K -> V)
  }

  def filter(dt: Any): List[Actor] = {
    val vl = dt.asInstanceOf[AnyRef].getClass
    objs.filter{ case (k, v) => v(dt) }.keys.toList
  }
}

class A extends Actor {
  override def start = {
    val actorId = super.start
    ActorRegistry.register(actorId, check _)
    actorId
  }

  def act() {
    loop {
      react {
	case v: Int =>
	  println("[A] incoming int " + v)
      }
    }
  }

  def check(data: Any): Boolean = {
    data match {
      case v: Int => true
      case _ => false
    }
  }
}

class B extends Actor {
  override def start = {
    val actorId = super.start
    ActorRegistry.register(actorId, check _)
    actorId
  }

  def act() {
    loop {
      react {
	case msg: String =>
	  println("[B] incoming str " + msg)
	case vl: Int =>
	  println("[B] incoming int " + vl)
      }
    }
  }

  def check(data: Any): Boolean = {
    data match {
      case v: String => true
      case l: Int => true
      case _ => false
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

  val data_list = List(10, "olur mu ya", List(1, 2, 3, 4), 6, 10)
  data_list.foreach { data => 
    val workers = ActorRegistry.filter(data)
    workers.foreach( _ ! data)
  }
}
