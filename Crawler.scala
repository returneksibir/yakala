import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.jcl.ArrayList

object Crawler {

	def selectAndPrintProductInfo(doc : Document) {

		val title = doc.title();
		val bookName = doc.select("div.kitaptitle2 > h1").first(); // a with href
		val bookPrice = doc.select("span.fiyat").first(); // a with href
		val isbn  = doc.select("span#ContentPlaceHolderMainOrta_LabelIsbn").first()

		try {
			println("------- " + title + " -------")
			println("Kitap = " + bookName.text())
			println("ISBN  = " + isbn.text())
			println("Fiyat = " + bookPrice.text())
		} catch {
			case e : NullPointerException => println("Düzgün biçimli kitap bilgisi bulunamadı.")
		}

	}

	def selectAndPrintProductLinks(doc : Document) {

		println("\n\nLinks on the page:")
		println("------------------")
		val links = doc.select("a[href]"); // a with href
		val iter = links.iterator()
		while(iter.hasNext()) {
			val link = iter.next()
			val text = link.ownText()
			var href = link.attr("href").toLowerCase()
			href = if (href.startsWith("http://")) href; else "http://www.pandora.com.tr" + href;
			if (!text.isEmpty() && href.startsWith("http://www.pandora.com.tr/urun/")) {
				println(text + " [ " + href + " ]")
			}
		}
	
	}

	def main(args : Array[String]) {
		val url = args(0)
		println(url + " adresindeki kitap fiyatını çıkartıyor...")
		val doc = Jsoup.connect(url).get();

		selectAndPrintProductInfo(doc)
		
		selectAndPrintProductLinks(doc)
	}
}

