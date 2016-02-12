package com.tbrown.twitterStream
import jsonz._
import org.joda.time.DateTime

case class User (
  location: Option[String],
  statusesCount: Int,
  lang: String,
  id: Long,
  favouritesCount: Int,
  description: Option[String],
  name: String,
  createdAt: DateTime,
  followersCount: Int,
  friendsCount: Int,
  screenName: String,
  idStr: String,
  profileImageUrl: String
)

object User extends JsonModule {
  implicit val UserFormat = productFormat13(
    "location",
    "statuses_count",
    "lang",
    "id",
    "favourites_count",
    "description",
    "name",
    "created_at",
    "followers_count",
    "friends_count",
    "screen_name",
    "id_str",
    "profile_image_url"
  )(User.apply)(User.unapply)
}

case class MediaElements (
  mediaUrlHttps: String,
  url: String
)

object MediaElements extends JsonModule {
  implicit val MediaElementsFormat = productFormat2("media_url_https", "url")(MediaElements.apply)(MediaElements.unapply)
}

case class ExtendedEntities (
  media: List[MediaElements]
)

object ExtendedEntities extends JsonModule {
  implicit val ExtendedEntitiesFormat = productFormat1("media")(ExtendedEntities.apply)(ExtendedEntities.unapply)
}

case class Tweet(
  retweeted: Boolean,
  lang: String,
  id: Long,
  extendedEntities: Option[ExtendedEntities],
  timestamp: String,
  createdAt: DateTime,
  favoriteCount: Int,
  text: String,
  source: String,
  retweetCount: Int,
  idStr: String,
  user: User
)

object Tweet extends JsonModule {
  implicit val tweetFormat = productFormat12(
    "retweeted",
    "lang",
    "id",
    "extended_entities",
    "timestamp_ms",
    "created_at",
    "favorite_count",
    "text",
    "source",
    "retweet_count",
    "id_str",
    "user")(Tweet.apply)(Tweet.unapply)
}

object TweetStreams extends Enumeration {
  val Sample, Filter = Value
}
