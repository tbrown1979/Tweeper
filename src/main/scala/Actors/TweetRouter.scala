package com.tbrown.twitterStream
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.json._
import DefaultJsonProtocol._


class TweetRouterActor extends Actor with ActorLogging {
  val metricActor  = StreamingActorSystem.actorOf(Props[TweetTrackerActor])
  val hashtagActor = StreamingActorSystem.actorOf(Props[HashtagActor])
  val emojiActor   = StreamingActorSystem.actorOf(Props[EmojiActor])
  val deadLetters  = StreamingActorSystem.deadLetters

  def receive: Receive = {
    case SampleStreamTweet(t, hts, emojis) =>
      metricActor ! TrackTweet
      hashtagActor ! hts
      emojiActor ! emojis

    case FilterStreamTweet(t, hts, emojis) =>
      TweetPersistence.storeTweet(t)
      hashtagActor ! hts
      emojiActor ! emojis
      context.system.eventStream.publish(t)
  }

  context.system.scheduler.schedule(0 seconds, 5 seconds, metricActor, ReportMetrics)

}
