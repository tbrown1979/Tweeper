package com.tbrown.twitterStream
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.json._
import DefaultJsonProtocol._


class TopicCheckerActor extends Actor with ActorLogging with TopicsConfig {

  def parseToTweet(json: String): Tweet =
    JsonParser(json).convertTo[Tweet]

  def logInfoFuture[A](f: Future[A], success: String, failure: String) =
    f onComplete {
      case Success(s) => log.info(success)
      case Failure(e) => log.error(failure)
    }

  def receive: Receive = {
    case tj@TweetJson(json) =>
      future(parseToTweet(json))
      context.system.eventStream.publish(tj)
  }

}
