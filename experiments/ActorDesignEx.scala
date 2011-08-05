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
  this.start
  
  override def start = {
    val actorId = super.start
    ActorRegistry.register(actorId, check _)
    actorId
  }

  def act() {
    loop {
      react {
	case (caller: Actor, v: Int) => {
	  println("[A] incoming int " + v + ", sending back in list")
	  caller ! (v :: Nil)
	}
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
  this.start
  
  override def start = {
    val actorId = super.start
    ActorRegistry.register(actorId, check _)
    actorId
  }

  def act() {
    loop {
      react {
	case (caller: Actor, msg: String) => {
	  println("[B] incoming str " + msg + ", sending back its length")
	  caller ! msg.length
	}
	case (caller: Actor, vl: Int) => {
	  println("[B] incoming int " + vl + ", sending back as List")
	  caller ! (vl :: Nil)
	}
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

object ActorDesignEx extends App with Actor {
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
  val actorId = this.start
  val data_list = List(10, "olur mu ya!@!@!", List(1, 2, 3, 4), 6, 10)
  println("sending initial data " + data_list)
  data_list.foreach { data => 
    val workers = ActorRegistry.filter(data)
    workers.foreach( _ ! (actorId, data))
  }

  def act() {
    loop {
      react {
	case  vl: Any => {
      	  val workers = ActorRegistry.filter(vl)
	  println("worker list :'" + workers + "', relevant data : " + vl)
      	  workers.foreach( _ ! (actorId, vl))
	}
      }
    }
  }
}
