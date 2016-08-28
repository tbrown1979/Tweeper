package com.tbrown.twitterStream
import twitter4j._

object Util {
  import ApiKeysConfig._

  val apiConfigBuilder = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessTokenKey)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .setJSONStoreEnabled(true)
    .build

  def defaultStatusListener(statusFunction: Tweet => Unit) = new StatusListener() {
    def onStatus(status: Status) = {
      import JsonModule._

      val json = TwitterObjectFactory.getRawJSON(status)
      val tweet = fromJsonStr[Tweet](json)
      tweet.foreach(t => statusFunction(t))
    }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(stallWarning: StallWarning) {}
  }
}
