import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.jcl.ArrayList

object Crawler {
	def main(args : Array[String]) {
		val url = args(0)
		println(url + " adresindeki kitap fiyatını çıkartıyor...")
		val doc = Jsoup.connect(url).get();
		val title = doc.title();
		val bookName = doc.select("div.kitaptitle2 > h1").first(); // a with href
		val bookPrice = doc.select("span.fiyat").first(); // a with href
		val isbn  = doc.select("span#ContentPlaceHolderMainOrta_LabelIsbn").first()
		val links = doc.select("a[href]"); // a with href
		val iter = links.iterator()

		println("------- " + title + " -------")
		println("Kitap = " + bookName.text())
		println("ISBN  = " + isbn.text())
		println("Fiyat = " + bookPrice.text())

		println("\n\nLinks on the page:")
		println("------------------")
		while(iter.hasNext()) {
			val text = iter.next().ownText()
			if (!text.isEmpty())
				println(text)
		}
	}
}

