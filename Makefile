
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
	yakala/registery/Registery.scala \
	yakala/Settings.scala

CP=-cp jsoup-1.6.1.jar:.
FLAGS=-deprecation

yakala: clean build run

clean:
	-find yakala/ -name "*.class" -exec rm {} \;

build:
	fsc $(FLAGS) $(CP) $(SOURCES)

run:
	time scala -cp ./jsoup-1.6.1.jar:. yakala.Yakala http://www.imge.com.tr http://www.pandora.com.tr
