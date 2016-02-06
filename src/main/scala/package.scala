package com.tbrown
// import akka.actor.Props
//import akka.actor.{Actor, ActorSystem, ActorRef}
// import akka.pattern.{ ask, pipe }
//import akka.routing.RoundRobinRouter
//import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import concurrent.ExecutionContext
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration._
import twitter4j._

package object twitterStream {

  val TwitterStreamingConfig = ConfigFactory.load()

//  lazy val StreamingActorSystem: ActorSystem = ActorSystem()

  val Success = scala.util.Success
  val Failure = scala.util.Failure

//  type ActorRefFactory = akka.actor.ActorRefFactory
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
