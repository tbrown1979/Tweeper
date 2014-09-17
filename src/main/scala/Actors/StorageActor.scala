package com.tbrown.twitterStream
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import scala.concurrent.duration._
import spray.can.Http
import spray.can.Http.RegisterChunkHandler
import spray.can.server.Stats
import spray.client.pipelining._
import spray.http._
import spray.util._
import HttpMethods._
import MediaTypes._

trait TweetPersistence {
  def storeTweet(tweet: String): Unit
}

trait ElasticSearchTweetPersistence extends TweetPersistence {
  //pull url out to application.conf
  val pipeline = sendReceive

  def storeTweet(tweet: String) = pipeline {
    Post("http://localhost:9200/tweets/tweet/", tweet)
  }
}

class TweetPersistenceActor extends TweetStorageActor with ElasticSearchTweetPersistence

trait TweetStorageActor extends Actor with ActorLogging with TweetPersistence {
  log.debug("Starting streaming response ...")

  // verride def preStart() = {
  //   context.system.eventStream.subscribe(context.self, classOf[TweetJson])
  // }

  def receive: Receive = {
    case TweetJson(json) =>
      storeTweet(json)
  }
}
