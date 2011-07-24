package yakala.pipelines

import yakala.logging.Logger


trait ItemPipeline {
  def processItem(item : Map[String, String])
}

