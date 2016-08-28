package com.tbrown.twitterStream

object Config {
  protected lazy val config = TwitterStreamingConfig

  def getString(key: String) = config.getString(key)
  def getInt(key: String) = config.getInt(key)
}

object Topics {
  import Config._

  val topics: List[String] = getString("topics").split(",").toList
}

object ApiKeysConfig {
  import Config._

  val consumerKey = getString("twitter.consumer.key")
  val consumerSecret = getString("twitter.consumer.secret")
  val accessTokenKey = getString("twitter.accessToken.key")
  val accessTokenSecret = getString("twitter.accessToken.secret")
}
