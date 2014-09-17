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

  def parseToTweet(json: String): Tweet =
    JsonParser(json).convertTo[Tweet]

  def receive: Receive = {
    case tj@TweetJson(json) =>
      future {
        parseToTweet(json)
      } onComplete {
        case Success(tweet) =>
          if (shouldPrint)
            log.info("TWEET PARSED :" + tweet)
          shouldPrint = false
        case Failure(e) => println(e)
      }
      context.system.eventStream.publish(tj)
    case "test" => println("test")
  }

}
