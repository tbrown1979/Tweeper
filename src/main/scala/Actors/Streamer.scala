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
import scala.reflect._
import scala.reflect.ClassTag

sealed trait JsonToClient {
  val json: String
}
case class TweetJson(json: String) extends JsonToClient
case class OtherJson(json: String) extends JsonToClient

class Streamer[T <: JsonToClient: ClassTag](client: ActorRef) extends Actor with ActorLogging {
  log.debug("Starting streaming response ...")

  //type typeOfStreaming = classTag[T].runtimeClass

  override def preStart = {
    context.system.eventStream.subscribe(context.self, classTag[T].runtimeClass)
  }

  client ! ChunkedResponseStart(HttpResponse(entity = ""))

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
