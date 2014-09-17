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
import HttpMethods._
import MediaTypes._
import MediaTypes._

class DemoService extends Actor with ActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self)

    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! HttpResponse(entity = "Hello!")

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path("/stream"), _, _, _) =>
      val peer = sender // since the Props creator is executed asyncly we need to save the sender ref
      context actorOf Props(new Streamer[SampleTweetJson](peer))

    case HttpRequest(GET, Uri.Path("/stream/filter"), _, _, _) =>
      val peer = sender // since the Props creator is executed asyncly we need to save the sender ref
      context actorOf Props(new Streamer[FilterTweetJson](peer))
  }
}
