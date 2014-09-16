package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import scala.util.{Success, Failure}
import spray.json._
import DefaultJsonProtocol._


class TopicCheckerActor extends Actor with ActorLogging with TopicsConfig {
  var shouldPrint = true

  def receive: Receive = {
    case TweetWithJson(tweet, json) =>
      if (shouldPrint) {
        println(tweet)
        shouldPrint = false
      }
      context.system.eventStream.publish(TweetJson(json))
    case "test" => println("test")
  }

}
