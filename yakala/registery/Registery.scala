package yakala.registery

import scala.actors.Actor

/*
 * Design of Registery object should be reconsidered.
 * Abstract type is essential for these check functions
 * and db.
 */
object Registery {
  private var db: Map[Actor, (Any => Boolean)] = Map()

  def register(K: Actor, V: (Any => Boolean)) {
    db += (K -> V)
  }

  def filter(dt: Any): List[Actor] = {
    db.filter{ case (k, v) => v(dt) }.keys.toList
  }
}

