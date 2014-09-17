package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import scala.util.{Success, Failure}
import spray.json._
import DefaultJsonProtocol._


class TweetRouterActor extends Actor with ActorLogging {
  val storageActor = StreamingActorSystem.actorOf(Props[TweetPersistenceActor])
  val topicChecker = StreamingActorSystem.actorOf(Props[TopicCheckerActor])
  val metricActor  = StreamingActorSystem.actorOf(Props[TweetTrackerActor])
  val deadLetters  = StreamingActorSystem.deadLetters//for testing without storing

  def receive: Receive = {
    case RouteTweet(json) =>
      storageActor ! TweetJson(json)
      topicChecker ! TweetJson(json)
      metricActor  ! TrackTweet
  }
}
