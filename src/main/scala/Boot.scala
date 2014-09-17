package com.tbrown.twitterStream
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import scala.util.Properties
import spray.can.Http
import twitter4j._


object Boot extends App with TopicsConfig{

  val twitterStreamSample = new TwitterStreamFactory(Util.config).getInstance
  val twitterStreamFilter = new TwitterStreamFactory(Util.config).getInstance
  val tweetRouter = StreamingActorSystem.actorOf(Props[TweetRouterActor])

  twitterStreamSample.addListener(Util.sampleStatusListener(tweetRouter))
  twitterStreamFilter.addListener(Util.filterStatusListener(tweetRouter))

  twitterStreamFilter.filter(new FilterQuery().track(topics.toArray))
  twitterStreamSample.sample

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  val service = StreamingActorSystem.actorOf(Props[DemoService], "demo-service")

  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}
