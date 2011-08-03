
SOURCES= \
	yakala/Yakala.scala \
	yakala/crawler/Crawler.scala \
	yakala/logging/Logger.scala \
	yakala/logging/ConsoleLogger.scala \
	yakala/pipelines/ItemPipeline.scala \
	yakala/pipelines/DummyBookDB.scala \
	yakala/pipelines/GoogleAppEngineBookDB.scala \
	yakala/spiders/Spider.scala \
	yakala/spiders/PandoraSpider.scala \
	yakala/spiders/ImgeSpider.scala \
	yakala/Settings.scala

CP=-cp jsoup-1.6.1.jar:.
FLAGS=-deprecation

yakala: clean build run

clean:
	-find yakala/ -name "*.class" | xargs rm

build:
	scalac $(FLAGS) $(CP) $(SOURCES)

run:
	time scala -cp ./jsoup-1.6.1.jar:. yakala.Yakala imge.com.tr pandora.com.tr
