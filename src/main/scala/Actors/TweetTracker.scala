package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.json._
import DefaultJsonProtocol._

case object PublishMetrics

class TweetTrackerActor extends Actor with ActorLogging {

  def receive: Receive = {
    case TrackTweet =>
      future {
        TweetMetrics.incrTweetCount
        TweetMetrics.markTweet
      }
    case ReportMetrics =>
      log.info(
        s"Tweet Count: ${TweetMetrics.getTweetCount}\n" +
        s"Rate of Tweets(1min): ${TweetMetrics.getOneMinuteRate} \n"+
        s"Rate of Tweets(overall): ${TweetMetrics.getMeanRate} \n"
      )
  }
}
