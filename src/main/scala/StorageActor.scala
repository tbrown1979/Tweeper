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
import scala.util.{Success, Failure}
import spray.can.Http.RegisterChunkHandler
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.index.IndexResponse
import spray.client.pipelining._

class TweetStorageActor extends Actor with ActorLogging with ElasticSearchService {
  log.debug("Starting streaming response ...")

  override def preStart() = {
    context.system.eventStream.subscribe(context.self, classOf[TweetJson])
  }

  var howMany = 0

  def receive: Receive = {
    case TweetJson(json) =>
      storeTweet(json) onComplete {
        case Success(s) =>
          howMany += 1
          log.info(s"ES response received:  $howMany")
        case Failure(t) =>
          log.error("Error received while trying to store to ES")
      }
  }
}


trait ElasticSearchService {
  //pull url out to application.conf
  val pipeline = sendReceive// ~> unmarshal[GoogleApiResult[Elevation]]

  def storeTweet(tweet: String) = pipeline {
    Post("http://localhost:9200/tweets/tweet/", tweet)
  }

}
