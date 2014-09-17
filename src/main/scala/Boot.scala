package com.tbrown.twitterStream
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import twitter4j._
import scala.util.Properties


object Boot extends App {

  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  val tweetRouter = StreamingActorSystem.actorOf(Props[TweetRouterActor])

  twitterStream.addListener(Util.simpleStatusListener(tweetRouter))
  twitterStream.sample

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  val service = StreamingActorSystem.actorOf(Props[DemoService], "demo-service")

  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}
