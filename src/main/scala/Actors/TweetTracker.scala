package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.json._
import DefaultJsonProtocol._

//case object PublishMetrics

// class TweetTrackerActor extends Actor with ActorLogging {
//   context.system.scheduler.schedule(0 seconds, 5 seconds, self, PublishStats)

//   def receive: Receive = {
//     case TrackTweet =>
//       future {
//         TweetMetrics.incrTweetCount
//         TweetMetrics.markTweet
//       }

//     case PublishStats =>
//       context.system.eventStream.publish(TweetMetrics.stats)

//     case ReportMetrics =>
//       log.info(
//         s"Tweet Count: ${TweetMetrics.getTweetCount}\n" +
//         s"Rate of Tweets(1min): ${TweetMetrics.getOneMinuteRate} \n"+
//         s"Rate of Tweets(overall): ${TweetMetrics.getMeanRate} \n"
//       )
//   }
// }
