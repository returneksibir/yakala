rm -rf yakala
scalac -deprecation -cp jsoup-1.6.1.jar:. Yakala.scala Crawler.scala Logger.scala BookDB.scala Spider.scala Settings.scala &&
time scala -cp ./jsoup-1.6.1.jar:. yakala.Yakala http://www.pandora.com.tr/Cok_Satan_Kitaplar
