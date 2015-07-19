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

trait ServiceActorComponent extends ServiceComponent with ActorModule {
  lazy val serviceActor = system.actorOf(Props(new ServiceActor))

  class ServiceActor extends Actor with HttpService with Service {
    def actorRefFactory = context

    def receive = runRoute(route)
  }
}

trait ServiceComponent extends TweetRepositoryComponent with ActorModule {

  trait Service extends Directives {

    import EventSourceService._

    def streamRoute[T](streamer: ActorRef => Streamer[T]) = {
      respondAsEventStream {
        ctx => {
          val peer = ctx.responder
          system actorOf Props(streamer(peer))
        }
      }
    }

    val route = {
      path("ping") {
        complete("pong")
      } ~
      path("stats") {
        streamRoute((peer: ActorRef) => new Streamer[StreamStats](peer))
      } ~
      pathPrefix("stream" / "filter") {
        parameters('lang.?, 'terms.?) { (lang, terms) =>
          val termList: List[String] = terms.fold(List[String]())(_.split("\\+").toList.map(t => t.toLowerCase))
          streamRoute((peer: ActorRef) => new TweetStreamer(peer, termList, lang))
        } ~
        pathEnd {
          streamRoute((peer: ActorRef) => new TweetStreamer(peer))
        }
      } ~
      path("search") {
        post {
          entity(as[SearchQuery]) { search =>
            complete(searchResults(search))
          }
        }
      }
    }

    def searchResults(search: SearchQuery): Future[List[Tweet]] =
      tweetRepository.search(search.size, search.from, search.searchTerms)

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
