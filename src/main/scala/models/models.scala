package com.tbrown.twitterStream
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import DateTimeJsonProtocol._

case object Report
case object ReportMetrics
case object TrackTweet
case class RouteTweet(json: String)
case class TweetWithJson(tweet: Tweet, json: String)

sealed trait JsonToClient {
  val json: String
}
case class SampleTweetJson(json: String) extends JsonToClient
case class FilterTweetJson(json: String) extends JsonToClient

case class TweetStreamStats(avg: Double, count: Long)

object TweetStreamStats extends DefaultJsonProtocol {
  implicit val StatsFormat = jsonFormat2(TweetStreamStats.apply)
}
