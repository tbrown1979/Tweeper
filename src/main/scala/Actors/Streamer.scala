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

class Streamer[T: JsonWriter: ClassTag](client: ActorRef) extends Actor with ActorLogging {
  import EventSourceService._

  log.debug("Starting streaming response ...")

  override def preStart = {
    context.system.eventStream.subscribe(context.self, classTag[T].runtimeClass)
  }

  val entity = HttpEntity(":\n\n")
  client ! ChunkedResponseStart(HttpResponse(entity=entity))

  def streamToClient[A: JsonWriter](toStream: A): Unit =
    client ! MessageChunk(formatAsSSE(toStream.toJson.toString))

  def streamerReceive: Receive = {
    case t: T => streamToClient(t)
  }

  def streamClosed: Receive =  {
    case x: Http.ConnectionClosed =>
      log.info("Canceling response stream due to {} ...", x)
      context.stop(self)
  }

  def receive = streamerReceive orElse streamClosed
}

class TweetStreamer(client: ActorRef, terms: List[String] = Nil, lang: Option[String] = None) extends Streamer[Tweet](client) {
  def isTerm(word: String): Boolean = terms.contains(word.toLowerCase)
  def matchLang(t: Tweet): Boolean = lang.fold(true)(_ == t.lang)
  def tweetHasMatchedTerms(t: Tweet): Boolean = {
    if (terms.isEmpty) true
    else {
      val tweetWords: List[String] = t.text.split(" ").toList
      tweetWords.foldLeft(false)((matched, word) => isTerm(word) || matched)
    }
  }
  override def streamerReceive: Receive = {
    case t: Tweet =>
      future {
        tweetHasMatchedTerms(t) && matchLang(t)
      } onComplete {
        case Success(p) => if (p) {
          log.debug("Sending down tweet")
          streamToClient(t)
        }
        case Failure(e) => log.error(s"Filter Streamer Failure: $e")
      }
  }

  override def receive = streamerReceive orElse streamClosed

}
