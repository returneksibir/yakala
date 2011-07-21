scalac -deprecation -cp jsoup-1.6.1.jar:. Crawler.scala Logger.scala BookDB.scala &&
time scala -cp ./jsoup-1.6.1.jar:. yakala.Crawler http://www.pandora.com.tr/Cok_Satan_Kitaplar
