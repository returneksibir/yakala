package yakala.utils

import collection.mutable.HashSet

class DefaultLinkSet extends SetTrait {

  private val set : HashSet[String] = HashSet.empty[String]

  def add(value : String) : Unit = set += value
  def contains(value : String) : Boolean = set.contains(value)
  def size() : Int = set.size

}

