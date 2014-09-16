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
