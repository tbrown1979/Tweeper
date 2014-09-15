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
import spray.json.DefaultJsonProtocol
//import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
//import spray.client.pipelining._

class TopicCheckerActor extends Actor with ActorLogging {// with TopicsConfig {

  override def preStart() = {
    context.system.eventStream.subscribe(context.self, classOf[TweetJson])
  }

  var shouldPrint = true

  def receive: Receive = {
    case TweetJson(json) => if (shouldPrint) {println(JsonParser(json)); shouldPrint = false}
    case "test" => println("test")
  }

}
