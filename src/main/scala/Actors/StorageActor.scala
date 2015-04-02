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
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.util._
import HttpMethods._
import MediaTypes._

trait TweetPersistence {
  def storeTweet(tweet: Tweet): Unit
  def searchTweets(size: Int, from: Int, searchTerms: List[String]): Future[List[Tweet]]
}

trait ElasticSearchTweetPersistence extends TweetPersistence with ElasticSearchConfig {
  implicit val system = StreamingActorSystem
  val pipeline = sendReceive

  def storeTweet(tweet: Tweet) = pipeline {
    Post(s"http://$url/tweets/tweet/", tweet)
  }

  def searchTweets(size: Int, from: Int, searchTerms: List[String]): Future[List[Tweet]] = {
    val searchQuery = if (searchTerms.isEmpty) "(*)" else s"(${searchTerms.mkString(" ")})"
    println(searchQuery)
    val esQuery = s"""{"size":${size},"from":${from},"query":{"query_string":{"default_field":"text","query":"${searchQuery} AND lang:en","default_operator":"AND"}},"sort":{"id":"desc"}}"""
    println(esQuery)
    val tweetListPipeline = sendReceive ~> unmarshal[EsSearchResult]
    tweetListPipeline(Post(s"http://$url/tweets/tweet/_search", esQuery))
      .map(res => res.hits.hits.map(_._source))
  }
}

object TweetPersistence extends ElasticSearchTweetPersistence
