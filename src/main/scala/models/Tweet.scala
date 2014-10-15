package com.tbrown.twitterStream
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport._
import spray.json._
import DateTimeJsonProtocol._

case class User (
  location: String,
  statuses_count: Int,
  lang: String,
  id: Long,
  favourites_count: Int,
  description: Option[String],
  name: String,
  created_at: DateTime,
  followers_count: Int,
  friends_count: Int,
  screen_name: String,
  id_str: String,
  profile_image_url: String
)

object User extends DefaultJsonProtocol {
  implicit val UserFormat = jsonFormat13(User.apply)
}

case class MediaElements (
  media_url_https: String,
  url: String
)

object MediaElements extends DefaultJsonProtocol {
  implicit val MediaElementsFormat = jsonFormat2(MediaElements.apply)
}

case class ExtendedEntities (
  media: List[MediaElements]
)

object ExtendedEntities extends DefaultJsonProtocol {
  implicit val ExtendedEntitiesFormat = jsonFormat1(ExtendedEntities.apply)
}

case class Tweet (
  retweeted: Boolean,
  lang: String,
  id: Long,
  extended_entities: Option[ExtendedEntities],
  timestamp_ms: String,
  created_at: DateTime,
  favorite_count: Int,
  text: String,
  source: String,
  retweet_count: Int,
  id_str: String,
  user: User
)

object Tweet extends DefaultJsonProtocol {
  implicit val TweetFormat = jsonFormat12(Tweet.apply)
}
