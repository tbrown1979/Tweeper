package com.tbrown.twitterStream

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties
import twitter4j._


object Boot extends App {

  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  //val storage = StreamingActorSystem.actorOf(Props[TweetPersistenceActor])
  val topicChecker = StreamingActorSystem.actorOf(Props[TopicCheckerActor])
  //val storage = StreamingActorSystem.actorOf(Props[TweetStatStorage])
  //val eventStream = StreamingActorSystem.eventStream
  val deadLetters = StreamingActorSystem.deadLetters//for testing without storing

  twitterStream.addListener(Util.simpleStatusListener(deadLetters, topicChecker))
  twitterStream.sample

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  val service = StreamingActorSystem.actorOf(Props[DemoService], "demo-service")

  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}
