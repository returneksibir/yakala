
SOURCES= \
	yakala/Yakala.scala \
	yakala/refdb/RefDb.scala \
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
	yakala/tools/Matcher.scala \
	yakala/Settings.scala

CP=-cp jsoup-1.6.1.jar:cglib-nodep-2.2.2.jar:h2-1.3.159.jar:squeryl_2.9.0-0.9.4-RC7.jar:postgresql-9.0-801.jdbc4.jar:.
FLAGS=-deprecation -unchecked

yakala: clean build run

clean:
	-find yakala/ -name "*.class" -exec rm {} \;

build:
	fsc $(FLAGS) $(CP) $(SOURCES)

run:
	time scala $(CP) yakala.Yakala imge.com.tr pandora.com.tr
