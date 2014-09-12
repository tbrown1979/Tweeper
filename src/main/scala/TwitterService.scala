package com.tbrown.twitterStream

import spray.httpx.marshalling.BasicMarshallers
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.http._
//import spray.client.pipelining._
import scala.util.{Success, Failure}
import akka.actor.{ActorContext}
import spray.httpx.SprayJsonSupport._
import spray.util._

class MyServiceActor extends Actor with MyService {
  // implicitly[ExceptionHandler](ExceptionHandler.default)
  implicitly[spray.routing.RoutingSettings](spray.routing.RoutingSettings.default)
  implicitly[ActorRefFactory](context)
  def actorRefFactory: ActorRefFactory = context

  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {

  val myRoute =
    path("") {
      get {
        complete {
          "hello"
        }
      }
    }
}
