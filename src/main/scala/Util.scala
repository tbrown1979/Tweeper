package com.tbrown.twitterStream
import twitter4j._
import akka.actor.ActorRef
import scala.util.{Success, Failure}
import twitter4j.TwitterObjectFactory

object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey("F0qSJRDC0wXhQ6FXeU3pZ5xJm")
    .setOAuthConsumerSecret("lx7zmWpOQ6aCc7k4P05IeDrjBmQSXhpApvbKogLr7voMHXkYNz")
    .setOAuthAccessToken("16603768-mYPj5hsiTAd2NJNAKOUrlb2eiA5w9povP0wqCzJS7")
    .setOAuthAccessTokenSecret("mVgKca5yTMALMYDbzxPsXSpM495f55ZW5WcSjTcv7SeZw")
    .setJSONStoreEnabled(true)
    .build

  //val twitterObjectFactory = new TwitterObjectFactory()

  def simpleStatusListener(storage: ActorRef) = new StatusListener() {
    def onStatus(status: Status) {
      println(TwitterObjectFactory.getRawJSON(status))
    }
    // def onStatus(status: Status) {
    //   future(new Tweet(status)).onComplete{
    //     case Success(t) => storage ! t
    //     case _ => "FAILED TWEET CAPTURE"}
    // }
    //def onStatus(status: Status) { storage ! status }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(stallWarning: StallWarning) {}
  }
}
