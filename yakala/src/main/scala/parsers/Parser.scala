// package yakala.parsers
// import org.xml.sax.{InputSource, XMLReader}
// import scales.utils.resources.SimpleUnboundedPool
// import scales.utils.top
// import scales.utils.collection.path.Path
// import scales.xml.parser.sax.DefaultSaxSupport
// import scales.xml._
// import ScalesXml._
// import scales.xml.jaxen.ScalesXPath

// object Parser {
//   def apply(source: InputSource) = {
//     val doc = loadXmlReader(source, strategy = defaultPathOptimisation,
//       parsers = NuValidatorFactoryPool)
//     val root = top(doc)
//     new ScalesParser(root)
//   }
// }

// trait Parser {
// //  def xpath(path: String)
// }

// class ScalesParser(root: XmlPath) extends Parser {
//   // def xpath(path: String): Any = ScalesXPath(path)
//   //   .withNameConversion(ScalesXPath.localOnly)
//   //   .evaluate(root).head.right.get.item.value
// }

// object NuValidatorFactoryPool extends
//     SimpleUnboundedPool[XMLReader] with DefaultSaxSupport {
//   def create = {
//     import nu.validator.htmlparser.{sax,common}
//     import sax.HtmlParser
//     import common.XmlViolationPolicy

//     val reader = new HtmlParser
//     reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
//     reader.setXmlnsPolicy(XmlViolationPolicy.ALLOW)
//     reader
//   }
// }
