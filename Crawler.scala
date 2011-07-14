import io.Source
import io._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.mutable.Set
import util.Random

object Crawler {
	
	val SITE_URL 			= "http://www.pandora.com.tr"
	val BOOK_NAME_PATH 		= "div.kitaptitle2 > h1"
	val BOOK_PRICE_PATH 	= "span.fiyat"
	val BOOK_ISBN_PATH 		= "span#ContentPlaceHolderMainOrta_LabelIsbn"
	val BOOK_PAGE_PATTERN	= "http://www.pandora.com.tr/urun/"
	val MIN_WAIT_TIME_IN_MS = 200
	val MAX_WAIT_TIME_IN_MS = 1000

	def selectAndPrintProductInfo(doc : Document) {

		val title 		= doc.title();
		val bookName 	= doc.select(BOOK_NAME_PATH).first()
		val bookPrice 	= doc.select(BOOK_PRICE_PATH).first()
		val isbn  		= doc.select(BOOK_ISBN_PATH).first()

		val txtBookPrice = try {
			val Price = """(\S+) TL""".r
			val Price(price) = bookPrice.text()
			price
		} catch {
			case e : MatchError => println("Price information is not in TL"); return
		}

		try {
			println("------- " + title + " -------")
			println("Kitap = " + bookName.text())
			println("ISBN  = " + isbn.text())
			println("Fiyat = " + txtBookPrice + " TL")
		} catch {
			case e : NullPointerException => println("Düzgün biçimli kitap bilgisi bulunamadı.")
		}

	}

	def selectAndPrintProductLinks(doc : Document, setOfLinksToBeVisited : Set[String], setOfLinksAlreadyVisited : Set[String]) {

		//println("\n\nLinks on the page:")
		//println("------------------")
		val links = doc.select("a[href]"); // a with href
		val iter = links.iterator()
		while(iter.hasNext()) {
			val link = iter.next()
			val text = link.ownText()
			var href = link.attr("href").toLowerCase()
			href = if (href.startsWith("http://")) href; else SITE_URL + href;
			if (!text.isEmpty() && href.startsWith(SITE_URL)) {
				if (!setOfLinksAlreadyVisited.contains(href))
					setOfLinksToBeVisited += href
				//println(text + " [ " + href + " ]")
			}
		}
	
	}

	def main(args : Array[String]) {
		val url = args(0)

		val setOfLinksToBeVisited : Set[String]	= Set(url)
		val setOfLinksAlreadyVisited : Set[String] = Set()

		val random = new Random()

		while (!setOfLinksToBeVisited.isEmpty) {
			setOfLinksToBeVisited foreach { url =>
			
				println("Sayfa :" + url)
	
				val doc = Jsoup.connect(url).get()

				if (url.startsWith(BOOK_PAGE_PATTERN))
					selectAndPrintProductInfo(doc)
				
				selectAndPrintProductLinks(doc, setOfLinksToBeVisited, setOfLinksAlreadyVisited)
	
				setOfLinksToBeVisited 		-= url
				setOfLinksAlreadyVisited 	+= url
	
				println("Gezilen   sayfa sayısı : " + setOfLinksAlreadyVisited.size)
				println("Gezilecek sayfa sayısı : " + setOfLinksToBeVisited.size)
	
				//Let's wait for a random time in the range between MIN_WAIT_TIME_IN_MS ~ MAX_WAIT_TIME_IN_MS ms
	
				val sleepTime = MIN_WAIT_TIME_IN_MS + Math.abs(random.nextInt()) % (MAX_WAIT_TIME_IN_MS - MIN_WAIT_TIME_IN_MS)
	
				Thread.sleep(sleepTime)
			}
		}
	}
}

