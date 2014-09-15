package com.tbrown.twitterStream

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties
import twitter4j._


object Boot extends App {

  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  val storage = StreamingActorSystem.actorOf(Props[TweetStorageActor])
  //val storage = StreamingActorSystem.actorOf(Props[TweetStatStorage])
  val eventStream = StreamingActorSystem.eventStream

  twitterStream.addListener(Util.simpleStatusListener(eventStream))
  twitterStream.sample

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  // create and start our service actor
  val service = StreamingActorSystem.actorOf(Props[DemoService], "demo-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}
