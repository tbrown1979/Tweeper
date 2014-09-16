package com.tbrown.twitterStream

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import spray.can.Http.RegisterChunkHandler

class Streamer(client: ActorRef, count: Int) extends Actor with ActorLogging {
  log.debug("Starting streaming response ...")

  override def preStart = {
    context.system.eventStream.subscribe(context.self, classOf[TweetJson])
  }

  client ! ChunkedResponseStart(HttpResponse(entity = ""))

  def receive = {
    case TweetJson(json) =>
      log.info("Sending Tweet json!")
      client ! MessageChunk(json)

    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }
}
