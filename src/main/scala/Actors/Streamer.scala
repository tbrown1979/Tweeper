package com.tbrown.twitterStream

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.reflect._
import spray.can.Http
import spray.can.Http.RegisterChunkHandler
import spray.can.server.Stats
import spray.http._
import spray.util._
//import HttpMethods._
import MediaTypes._

object EventSourceService {
  val `text/event-stream` = register(
    MediaType.custom(
      mainType = "text",
      subType  = "event-stream"
    )
  )
  // val `text/event-stream` = MediaType.custom("text/event-stream")

  // MediaTypes.register(`text/event-stream`)
}

class Streamer[T <: JsonToClient: ClassTag](client: ActorRef) extends Actor with ActorLogging {
  import EventSourceService._

  log.debug("Starting streaming response ...")

  override def preStart = {
    context.system.eventStream.subscribe(context.self, classTag[T].runtimeClass)
  }

  val pentity = HttpEntity(`text/event-stream`, "")
  client ! ChunkedResponseStart(HttpResponse(entity = pentity))

  def receive = {
    case jsonModel: T =>
      val json = jsonModel.json
      log.info("Sending Tweet json!")
      client ! MessageChunk(json)

    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }
}
