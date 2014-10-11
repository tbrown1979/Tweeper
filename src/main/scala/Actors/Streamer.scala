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
import spray.json._
import MediaTypes._
import spray.routing.directives.RespondWithDirectives._
import spray.http.ContentTypes._
import HttpHeaders.{`Cache-Control`, `Connection`}
import CacheDirectives.`no-cache`

object EventSourceService {
  val `text/event-stream` = MediaType.custom("text/event-stream")
  MediaTypes.register(`text/event-stream`)

  def formatAsSSE(data: String): String =
    s"data:$data\n\n"

  def respondAsEventStream = {
    respondWithHeader(`Cache-Control`(`no-cache`)) &
    respondWithHeader(`Connection`("Keep-Alive")) &
    respondWithMediaType(`text/event-stream`)
  }
}

abstract class Streamer[T: JsonWriter: ClassTag](client: ActorRef) extends Actor with ActorLogging {
  import EventSourceService._

  log.debug("Starting streaming response ...")

  override def preStart = {
    context.system.eventStream.subscribe(context.self, classTag[T].runtimeClass)
  }

  val entity = HttpEntity(":\n\n")
  client ! ChunkedResponseStart(HttpResponse(entity=entity))

  def streamToClient[A: JsonWriter](toStream: A): Unit =
    client ! MessageChunk(formatAsSSE(toStream.toJson.toString))

  def streamerReceive: Receive

  def streamClosed: Receive =  {
    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }

  def receive = streamerReceive orElse streamClosed
}

class GenericStreamer[T: JsonWriter: ClassTag](client: ActorRef) extends Streamer[T](client) {
  def streamerReceive: Receive = {
    case t: T => streamToClient(t)
  }
}

class FilterTweetStreamer(client: ActorRef, terms: List[String]) extends Streamer[Tweet](client) {
  def isTerm(word: String): Boolean = terms.contains(word.toLowerCase)
  def streamerReceive: Receive = {
    case t: Tweet =>
      future {
        val tweetWords: List[String] = t.text.split(" ").toList
        tweetWords.foldLeft(false)((matched, word) => isTerm(word) || matched)
      } onComplete {
        case Success(p) => if (p) streamToClient(t)
        case Failure(e) => log.error(s"Filter Streamer Failure: $e")
      }
  }
}
