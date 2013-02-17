
ARCHIVE=yakala.jar

SOURCES= \
	yakala/crawler/Crawler.scala \
	yakala/logging/Logger.scala \
	yakala/logging/ConsoleLogger.scala \
	yakala/pipelines/ItemPipeline.scala \
	yakala/spiders/Spider.scala \
	yakala/utils/SetTrait.scala \
	yakala/utils/DefaultLinkSet.scala \
	yakala/utils/BloomFilterLinkSet.scala \
	yakala/Settings.scala

CP=-cp lib/*:.
FLAGS=-deprecation -unchecked

yakala: clean build

clean:
	-find yakala/ -name "*.class" -exec rm {} \;

build:
	fsc $(FLAGS) $(CP) $(SOURCES)
	find yakala/ -name "*.class" | xargs jar cf $(ARCHIVE)

