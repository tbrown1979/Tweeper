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
  //get rid of duplication
  def filterStatusListener(router: ActorRef) =
    defaultStatusListener(
      (status: Status) => {
        val json = TwitterObjectFactory.getRawJSON(status)
        val tweet = JsonParser(json).convertTo[Tweet]
        val hashtags = Hashtags(status.getHashtagEntities.toList.map(_.getText))
        val emojis = Emoji.findEmojis(status.getText)
        router ! FilterStreamTweet(tweet, hashtags, emojis)
      }
    )

  def sampleStatusListener(router: ActorRef) =
    defaultStatusListener(
      (status: Status) => {
        val json = TwitterObjectFactory.getRawJSON(status)
        val tweet = JsonParser(json).convertTo[Tweet]
        val hashtags = Hashtags(status.getHashtagEntities.toList.map(_.getText))
        val emojis = Emoji.findEmojis(status.getText)
        router ! SampleStreamTweet(tweet, hashtags, emojis)
      }
    )
}
