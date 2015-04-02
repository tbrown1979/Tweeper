package com.tbrown.twitterStream

trait Config {
  protected lazy val config = TwitterStreamingConfig

  def getString(key: String) = config.getString(key)
  def getInt(key: String) = config.getInt(key)
}

trait ElasticSearchConfig extends Config {
  lazy val host = getString("elasticsearch.host")
  lazy val port = getString("elasticsearch.port")
  lazy val url = s"$host:$port"
}

trait TopicsConfig extends Config {
  val topics: List[String] = getString("topics").split(",").toList
}

trait ApiKeysConfig extends Config {
  val consumerKey = getString("twitter.consumer.key")
  val consumerSecret = getString("twitter.consumer.secret")
  val accessTokenKey = getString("twitter.accessToken.key")
  val accessTokenSecret = getString("twitter.accessToken.secret")
}
