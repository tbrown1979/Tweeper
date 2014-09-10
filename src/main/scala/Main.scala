package com.tbrown
import java.util.concurrent.TimeUnit;

import akka.actor.{Actor, ActorSystem, ActorRef}
import akka.routing.RoundRobinRouter
import akka.actor.Props
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import twitter4j._

object Main extends App {
  val system = ActorSystem()
  import system.dispatcher
  
  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  val storage = system.actorOf(Props[TweetStatStorage])

  twitterStream.addListener(Util.simpleStatusListener(storage))
  twitterStream.sample
  Thread.sleep(5000)
  system.scheduler.schedule(0 seconds, 5 seconds, storage, Report)
}
