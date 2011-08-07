package yakala.tools

import scala.reflect._

/*
 * This level1 depth match function.
 * You cannot use this function in pattern match for 
 *	List[List[Int]] against List[List[Any]]
 */
class Matcher[T](implicit matcherType: Manifest[T]) {
  def unapply[U](param: U)(implicit actualType: Manifest[U]): Option[T] = {
    def elemCheck = {
      matcherType.typeArguments.zip(actualType.typeArguments).forall {
	case (matcher, actual) => matcher >:> actual
      }
    }

    if ((matcherType >:> actualType) && elemCheck)
      Some(param.asInstanceOf[T])
    else
      None
  }
}
