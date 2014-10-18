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
case class FilterStreamTweet(tweet: Tweet, hashtags: Hashtags, emojis: Emojis)
case class SampleStreamTweet(tweet: Tweet, hashtags: Hashtags, emojis: Emojis)
case class Hashtags(value: List[String])
case class Emojis(value: List[Emoji])
//case class TopElements[A: JsonWriter](value: List[A])

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
  query: String
)
object SearchQuery extends DefaultJsonProtocol {
  implicit val formats = jsonFormat3(SearchQuery.apply)
}

case class SortProperties(
  id: String
)
object SortProperties extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(SortProperties.apply)
}

case class QueryProperties(
  default_field: String,
  query: String,
  default_operator: String
)
object QueryProperties extends DefaultJsonProtocol {
  implicit val formats = jsonFormat3(QueryProperties.apply)
}

case class Query(
  query_string: QueryProperties
)
object Query extends DefaultJsonProtocol {
  implicit val formats = jsonFormat1(Query.apply)
}

case class EsSearch(
  size: Int,
  from: Int,
  query: Query,
  sort: SortProperties
)
object EsSearch extends DefaultJsonProtocol {
  implicit val formats = jsonFormat4(EsSearch.apply)
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
