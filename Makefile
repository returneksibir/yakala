
SOURCES= \
	yakala/Yakala.scala \
	yakala/crawler/Crawler.scala \
	yakala/logging/Logger.scala \
	yakala/logging/ConsoleLogger.scala \
	yakala/db/BookDB.scala \
	yakala/spiders/Spider.scala \
	yakala/Settings.scala

CP=-cp jsoup-1.6.1.jar:.
FLAGS=-deprecation

yakala: clean build run

clean:
	-find yakala/ -name "*.class" | xargs rm

build:
	scalac $(FLAGS) $(CP) $(SOURCES)

run:
	time scala -cp ./jsoup-1.6.1.jar:. yakala.Yakala http://www.pandora.com.tr/Cok_Satan_Kitaplar
