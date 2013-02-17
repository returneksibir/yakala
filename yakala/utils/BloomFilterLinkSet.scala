package yakala.utils

import com.skjegstad.utils.BloomFilter

class BloomFilterLinkSet(falsePositiveProbability : Double, expectedSize : Int) extends SetTrait {

  private val set : BloomFilter[String] = new BloomFilter[String](falsePositiveProbability, expectedSize)

  def add(value : String) : Unit = set.add(value)
  def contains(value : String) : Boolean = set.contains(value)
  def size() : Int = set.size

}

