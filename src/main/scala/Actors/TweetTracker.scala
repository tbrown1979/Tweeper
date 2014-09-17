package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import scala.util.{Success, Failure}
import spray.json._
import DefaultJsonProtocol._

class TweetTrackerActor extends Actor with ActorLogging {

  def receive: Receive = {
    case TrackTweet =>
      TweetMetrics.incrTweetCount
      TweetMetrics.markTweet
    case ReportMetrics =>
      log.info(
        s"Tweet Count: ${TweetMetrics.getTweetCount}" +
        s"Rate of Tweets(1min): ${TweetMetrics.getOneMinuteRate}"
      )
  }
}
