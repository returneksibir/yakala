import scala.xml.Source
import org.scalatest.FlatSpec
import org.scalatest._
import yakala.parsers.HtmlParser

class ParserSpec extends FlatSpec {

  "A HtmlParser" should "support xpath" in {
    val src = Source.fromString("""
    <html xmlns="http://www.w3.org/1999/xhtml" 
xml:lang="en"><head/><body><div id="content"><div 
class="main">foo</div></div></body></html> 
""")
    val parser = new HtmlParser(src)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[String]
    intercept[NoSuchElementException] {
      emptyStack.pop()
    }
  }
}
