package com.tbrown.twitterStream
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import scala.util.Properties
import spray.can.Http
import twitter4j._


object Boot extends App
  with TopicsConfig
  with AkkaServiceActorComponent
  with ElasticsearchTweetRepositoryComponent
  with TweetStreamListeners
  with TweeperActorModule {

  val twitterStreamSample = new TwitterStreamFactory(Util.apiConfigBuilder).getInstance
  val twitterStreamFilter = new TwitterStreamFactory(Util.apiConfigBuilder).getInstance

  twitterStreamSample.addListener(sampleStatusListener)
  twitterStreamFilter.addListener(filterStatusListener(system))

  twitterStreamFilter.filter(new FilterQuery().track(topics.toArray))
  twitterStreamSample.sample

  val port = Properties.envOrElse("PORT", "8080").toInt

  IO(Http) ! Http.Bind(serviceActor, "0.0.0.0", port)
}
