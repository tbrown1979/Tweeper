package com.tbrown.twitterStream

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties


object Boot extends App {
  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  implicit val system = StreamingActorSystem

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}
