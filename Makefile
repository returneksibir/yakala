
ARCHIVE=yakala.jar

SOURCES= \
	yakala/crawler/Crawler.scala \
	yakala/logging/Logger.scala \
	yakala/logging/ConsoleLogger.scala \
	yakala/pipelines/ItemPipeline.scala \
	yakala/spiders/Spider.scala \
	yakala/Settings.scala

CP=-cp jsoup-1.6.1.jar:.
FLAGS=-deprecation -unchecked

yakala: clean build

clean:
	-find yakala/ -name "*.class" | xargs rm

build:
	fsc $(FLAGS) $(CP) $(SOURCES)
	find yakala/ -name "*.class" | xargs jar cf $(ARCHIVE)

