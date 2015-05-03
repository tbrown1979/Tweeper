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
case class FilterStreamTweet(tweet: Tweet)//, hashtags: Hashtags, emojis: Emojis)
case class SampleStreamTweet(tweet: Tweet)//, hashtags: Hashtags, emojis: Emojis)
case class Hashtags(value: List[String])
case class Emojis(value: List[Emoji])

case class Hashtag(hashtag: String)
object Hashtag extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(Hashtag.apply)
}

case class StreamStats(avg: Double, count: Long)

object StreamStats extends DefaultJsonProtocol {
  implicit val formats = jsonFormat2(StreamStats.apply)
}

case class SearchQuery(
  size: Int,
  from: Int,
  searchTerms: List[String]
)
object SearchQuery extends DefaultJsonProtocol {
  implicit val formats = jsonFormat3(SearchQuery.apply)
}

case class EsTweet(
  _source: Tweet
)
object EsTweet extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(EsTweet.apply)
}

case class TweetHits(
  hits: List[EsTweet]
)
object TweetHits extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(TweetHits.apply)
}

case class EsSearchResult(
  hits: TweetHits
)
object EsSearchResult extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(EsSearchResult.apply)
}
