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

class ServiceActor extends Actor with ActorLogging with ServiceRoute {// with FrontendContentRoute {
  def actorRefFactory = context

  val route =
    //contentRoute ~
    serviceRoute

  def receive = runRoute(route)
}

trait ServiceRoute extends HttpService {
  import EventSourceService._

  def streamRoute[T](streamer: ActorRef => Streamer[T]) = {
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
      respondAsEventStream {
        ctx => {
          val peer = ctx.responder
          actorRefFactory actorOf Props(streamer(peer))
        }
      }
    }
  }
  val serviceRoute = {
    path("ping") {
      complete("PONG!")
    } ~
    path("stats") {
      streamRoute((peer: ActorRef) => new GenericStreamer[StreamStats](peer))
    } ~
    pathPrefix("stream" / "filter") {
      parameters('lang.?, 'terms.?) { (lang, terms) =>
        val termList: List[String] =
          terms.fold(List[String]())(_.split("\\+").toList.map(t => t.toLowerCase))//use unmarshalling?
        streamRoute((peer: ActorRef) => new TweetStreamer(peer, termList, lang))
      } ~
      pathEnd {
        streamRoute((peer: ActorRef) => new TweetStreamer(peer))
      }
    } ~
    path("search") {
      respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
        post {
          entity(as[SearchQuery]) { search =>
            val size = search.size
            val from = search.from
            val searchTerms = search.searchTerms

            complete(TweetPersistence.searchTweets(size, from, searchTerms))
          }
        }
      }
    }
    //  ~
    // pathPrefix("top") {
    //   path("emojis") {
    //     respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
    //     complete(EmojiTracker.topElements(3))
    //     }
    //   } ~
    //   path("hashtags") {
    //     respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
    //     complete(HashtagTracker.topElements(3).map(hts => hts.map(Hashtag(_))))
    //     }
    //   }
    // }
  }
}

trait FrontendContentRoute extends HttpService {
  val contentRoute = {
    path("") {
      getFromResource("www/index.html")
    } ~
    pathPrefix("www") { get { getFromResourceDirectory("www") } }
  }
}
