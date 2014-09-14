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

  // we use the successful sending of a chunk as trigger for scheduling the next chunk
  client ! ChunkedResponseStart(HttpResponse(entity = " " * 2048)).withAck(Ok(count))

  def receive = {
    case Ok(0) =>
      log.info("Finalizing response stream ...")
      client ! MessageChunk("\nStopped...")
      client ! ChunkedMessageEnd
      context.stop(self)

    case Ok(remaining) =>
      log.info("Sending response chunk ...")
      (1 to remaining).toList.foreach(n => {Thread.sleep(10); client ! MessageChunk(s"HEY! $n").withAck(remaining-n)})
      // context.system.scheduler.scheduleOnce(1 millis span) {
      //   client ! MessageChunk(DateTime.now.toIsoDateTimeString + ", ").withAck(Ok(remaining - 1))
      //}

    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }

  // simple case class whose instances we use as send confirmation message for streaming chunks
  case class Ok(remaining: Int)
}
