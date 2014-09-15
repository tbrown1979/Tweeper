package com.tbrown.twitterStream

trait Config {
  protected lazy val config = TwitterStreamingConfig

  def getString(key: String) = config.getString(key)
  def getInt(key: String) = config.getInt(key)
}

trait ElasticSearchConfig extends Config {
  //nothing here yet
}

trait TopicsConfig extends Config {
  val topics: List[String] = getString("topics").split(",").toList
}
