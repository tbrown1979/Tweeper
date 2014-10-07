package com.tbrown.twitterStream

import akka.actor._
import akka.actor.{ActorContext, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.can.Http
import spray.can.Http.RegisterChunkHandler
import spray.can.server.Stats
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.BasicMarshallers
import spray.routing._
import spray.util._
import HttpHeaders._
import HttpMethods._
import MediaTypes._
import MediaTypes._

class ServiceActor extends Actor with ActorLogging with StatsRoute with FrontendContentRoute {
  def actorRefFactory = context

  val route =
    contentRoute ~
    statsRoute

  def receive = runRoute(route)
}

trait StatsRoute extends HttpService {
  import EventSourceService._

  def streamRoute[T](stringPath: String, streamer: ActorRef => Streamer[T]) = {
    path(stringPath) {
      respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
        respondAsEventStream {
          ctx => {
            val peer = ctx.responder
            actorRefFactory actorOf Props(streamer(peer))
          }
        }
      }
    }
  }


  val statsRoute = {
    path("hello") {
      getFromResource("index.html")
    } ~
    path("ping") {
      complete("PONG!")
    } ~
    streamRoute("stats", (peer: ActorRef) => new GenericStreamer[StreamStats](peer)) ~//duplication
    pathPrefix("stream" / "filter") {
      respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
        respondAsEventStream {
          pathEnd {
            ctx => {
              val peer = ctx.responder
              actorRefFactory actorOf Props(new GenericStreamer[Tweet](peer))
            }
          } ~
          path(Segment) { filterTerm =>
            println(filterTerm)
            ctx => {
              val peer = ctx.responder
              actorRefFactory actorOf Props(new FilterTweetStreamer(peer, filterTerm))
            }
          }
        }
      }
    }
  }
}

trait FrontendContentRoute extends HttpService {
  val contentRoute = {
    path("index") { getFromResource("index.html") } ~
    pathPrefix("config.js") { get { getFromResource("config.js") } } ~
    pathPrefix("vendor") { get { getFromResourceDirectory("vendor") } } ~
    pathPrefix("css") { get { getFromResourceDirectory("css") } } ~
    pathPrefix("app") { get { getFromResourceDirectory("app") } } ~
    pathPrefix("img") { get { getFromResourceDirectory("img") } } ~
    pathPrefix("js") { get { getFromResourceDirectory("js") } } ~
    pathPrefix("font") { get { getFromResourceDirectory("font") } }
  }
}
