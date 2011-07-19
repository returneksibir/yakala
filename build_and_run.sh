scalac -deprecation -cp jsoup-1.6.1.jar Crawler.scala &&
time scala -cp ./jsoup-1.6.1.jar:. Crawler http://www.pandora.com.tr/Cok_Satan_Kitaplar
