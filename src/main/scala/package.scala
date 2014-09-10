package com.tbrown

import akka.actor.Props
import akka.actor.{Actor, ActorSystem, ActorRef}
import akka.pattern.{ ask, pipe }
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import concurrent.ExecutionContext
//import concurrent.Future
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.util.{Success, Failure}
import twitter4j._


package object twitterStream {
  //val streamingConfig = ConfigFactory.load() don't use yet...

  implicit lazy val StreamingActorSystem: ActorSystem = ActorSystem()

  implicit lazy val ExecutionContext: ExecutionContext = StreamingActorSystem.dispatcher

  type ActorRefFactory = akka.actor.ActorRefFactory
  type Future[T] = scala.concurrent.Future[T]
  val Future = scala.concurrent.Future

  def future[T](f: => T)(implicit ec: ExecutionContext): Future[T] = {
    val future = Future.apply(f)(ec)
    future.onFailure {
      case t: Throwable => println("Exception from the future")
    } (ec)
    future
  }
}
