package com.tbrown.twitterStream

import java.util.concurrent.Executors

import fs2.{Strategy, Task}

import org.http4s.server._
import org.http4s.server.blaze.BlazeBuilder
import org.log4s.getLogger

import scala.util.Properties.envOrNone

class Boot(host: String, port: Int) extends Routes {

  private val logger = getLogger
  private val pool = Executors.newCachedThreadPool()

  logger.info(s"Starting Http4s-blaze example on '$host:$port'")

  implicit val strategy = Strategy.fromExecutor(pool)

  Tweets.stream[Task].map(println).run.unsafeRun

  //Construct the blaze pipeline.
  def build: ServerBuilder =
    BlazeBuilder.bindHttp(port, host)
      .withWebSockets(true)
      .mountService(websocketRoutes.service)
      .withServiceExecutor(pool)
}

object Boot extends App {
  val ip =   "0.0.0.0"
  val port = envOrNone("HTTP_PORT") map(_.toInt) getOrElse 8080

   new Boot(ip, port)
   //  .build
     //.run
     //.awaitShutdown()
}
