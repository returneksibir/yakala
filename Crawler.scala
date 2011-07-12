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
		println(title)
		val links = doc.select("span.fiyat"); // a with href
		val iter = links.iterator()
		while (iter.hasNext()) {
			println(iter.next().text())
		}
	}
}

