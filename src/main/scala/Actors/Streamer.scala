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

  // we use the successful sending of a chunk as trigger for scheduling the next chunk
  client ! ChunkedResponseStart(HttpResponse(entity = " " * 2048))//.withAck(Ok(count))

  def receive = {
    case Ok(0) =>
      log.info("Finalizing response stream ...")
      client ! MessageChunk("\nStopped...")
      client ! ChunkedMessageEnd
      context.stop(self)

    case Ok(remaining) =>
      log.info("Sending response chunk ...")

    case TweetJson(json) =>
      log.info("Sending Tweet json!")
      client ! MessageChunk(json)

    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }

  // simple case class whose instances we use as send confirmation message for streaming chunks
  case class Ok(remaining: Int)
}
