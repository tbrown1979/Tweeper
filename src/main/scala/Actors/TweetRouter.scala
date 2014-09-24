package com.tbrown.twitterStream
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.json._
import DefaultJsonProtocol._


class TweetRouterActor extends Actor with ActorLogging {
  val storageActor = StreamingActorSystem.actorOf(Props[TweetPersistenceActor])
  val metricActor  = StreamingActorSystem.actorOf(Props[TweetTrackerActor])
  val deadLetters  = StreamingActorSystem.deadLetters

  def receive: Receive = {
    case tj: SampleTweetJson =>
      //metricActor ! TrackTweet

    case ft: FilterTweetJson =>
      //storageActor ! ft
      context.system.eventStream.publish(ft)
  }

  context.system.scheduler.schedule(0 seconds, 5 seconds, metricActor, ReportMetrics)

}
