package yakala.registery

import scala.actors.Actor

object Registery {
  private var db: Map[Actor, (Any => Boolean)] = Map()

  def register(K: Actor, V: (Any => Boolean)) {
    db += (K -> V)
  }

  def filter(dt: Any): List[Actor] = {
    db.filter{ case (k, v) => v(dt) }.keys.toList
  }
}

