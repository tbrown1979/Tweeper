package com.tbrown.twitterStream
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import scala.util.{Success, Failure}
import spray.json._
import DefaultJsonProtocol._


class TopicCheckerActor extends Actor with ActorLogging {// with TopicsConfig {

  override def preStart() = {
    context.system.eventStream.subscribe(context.self, classOf[TweetJson])
  }

  var shouldPrint = true

  def receive: Receive = {
    case TweetJson(json) => if (shouldPrint) {println(JsonParser(json).convertTo[Tweet]); shouldPrint = false}
    case "test" => println("test")
  }

}
