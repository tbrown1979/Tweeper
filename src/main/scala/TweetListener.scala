package com.tbrown.twitterStream
import akka.actor._
import spray.json._
import twitter4j._
import DefaultJsonProtocol._

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

trait TweetStreamListeners extends TweetRepositoryComponent {
  import Util._

  def filterStatusListener(system: ActorSystem) =
    defaultStatusListener(
      (status: Status) => {//future {
        val json = TwitterObjectFactory.getRawJSON(status)
        val tweet = JsonParser(json).convertTo[Tweet]
        tweetRepository.store(tweet)
        system.eventStream.publish(tweet)
        TweetMetrics.incrTweetCount(TweetStreams.Filter)
      }
    )

  def sampleStatusListener =
    defaultStatusListener(
      (status: Status) => {//future {
        val json = TwitterObjectFactory.getRawJSON(status)
        val tweet = JsonParser(json).convertTo[Tweet]
        TweetMetrics.incrTweetCount(TweetStreams.Sample)
        TweetMetrics.markTweet(TweetStreams.Sample)
      }
    )
}
