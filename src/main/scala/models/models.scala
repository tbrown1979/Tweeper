package com.tbrown.twitterStream
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import DateTimeJsonProtocol._

case object Report
case object ReportMetrics
case object TrackTweet
case object PublishStats
case class RouteTweet(json: String)
case class PersistTweet(tweet: Tweet)

case class StreamStats(avg: Double, count: Long)

object StreamStats extends DefaultJsonProtocol {
  implicit val StatsFormat = jsonFormat2(StreamStats.apply)
}
