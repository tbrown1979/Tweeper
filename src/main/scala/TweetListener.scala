package com.tbrown.twitterStream
import twitter4j._

object Util extends ApiKeysConfig {
  val apiConfigBuilder = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessTokenKey)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .setJSONStoreEnabled(true)
    .build

  def defaultStatusListener(statusFunction: Status => Unit) = new StatusListener() {
    def onStatus(status: Status) = statusFunction(status)
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(stallWarning: StallWarning) {}
  }
}

trait TweetStreamListeners extends TweetStreamComponent with JsonModule {
  import Util._

  def filterStatusListener =
    defaultStatusListener(
      (status: Status) => {//task {
        import jsonz._
        val json = TwitterObjectFactory.getRawJSON(status)
        val tweet = fromJsonStr[Tweet](json)
        tweet.foreach(stream.publishOne(_))
      }
    )

  // def sampleStatusListener =
  //   defaultStatusListener(
  //     (status: Status) => {//future {
  //       // val json = TwitterObjectFactory.getRawJSON(status)
  //       // val tweet = JsonParser(json).convertTo[Tweet]
  //       // TweetMetrics.incrTweetCount(TweetStreams.Sample)
  //       // TweetMetrics.markTweet(TweetStreams.Sample)
  //     }
  //   )
}
