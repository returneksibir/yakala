package yakala.document
import org.jaxen.{DefaultNavigator, XPath, BaseXPath}
import java.util.{Iterator=>JIterator}
import scala.xml._

class DocumentNavigator extends DefaultNavigator {
  implicit def wrap[T <: AnyRef](iter: Iterator[T]) = new JIterator[T] {
    def remove = throw new UnsupportedOperationException
    def next = iter.next
    def hasNext = iter.hasNext
  }

  override def getAttributeName(attr: Any) = attr match {
    case md: MetaData => md.key
    case _ => null
  }
  override def getAttributeAxisIterator(contextNode: AnyRef): JIterator[AnyRef] = contextNode match {
    case Elem(_, _, attributes, _, _*) => attributes.iterator
    case _ => throw new java.util.NoSuchElementException
  }
  override def getAttributeNamespaceUri(attr: Any) = null
  override def getAttributeQName(attr: Any) = null
  override def getAttributeStringValue(attr: Any) = attr match {
    case md: MetaData => md.value.text
    case _=> null
  }
  override def getChildAxisIterator(ctx: AnyRef): JIterator[AnyRef] = ctx match {
    case Elem(_, _, _, _, _, children @ _*) => children.iterator
    case _ => throw new java.util.NoSuchElementException
  }
  override def getCommentStringValue(comment: Any) = comment match {
    case c: Comment => c.text
  }
  override def getElementName(elem: Any) = elem match {
    case Elem(_, name, _, _, _, _*) => name
  }
  override def getElementNamespaceUri(elem: Any) = ""
  override def getElementQName(elem: Any) = null
  override def getElementStringValue(elem: Any) = elem match {
    case ns: NodeSeq => ns.text
  }
  override def getNamespacePrefix(ns: Any) = ""
  override def getNamespaceStringValue(ns: Any) = ""
  override def getTextStringValue(text: Any) = text match {
    case ns: NodeSeq => ns.text
  }
  override def isAttribute(any: AnyRef) = any.isInstanceOf[MetaData]
  override def isComment(any: AnyRef) = any.isInstanceOf[Comment]
  override def isDocument(any: AnyRef) = any.isInstanceOf[Document]
  override def isElement(any: AnyRef) = any.isInstanceOf[Elem]
  override def isNamespace(any: AnyRef) = false
  override def isProcessingInstruction(any: AnyRef) = any.isInstanceOf[ProcInstr]
  override def isText(any: AnyRef) = any.isInstanceOf[Text]
  override def parseXPath(expr: String) = new DocumentXPath(expr)
}

object DocumentNavigator extends DocumentNavigator

class DocumentXPath(expr: String) extends BaseXPath(expr, DocumentNavigator)

class DocumentNodeSeq(val self: NodeSeq) {
  def createXPath(expr: String) = new DocumentXPath(expr)
}

object DocumentNodeSeq {
  implicit def xmlToDocumentNodeSeq(ns: NodeSeq) = new DocumentNodeSeq(ns)
}
