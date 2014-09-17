package com.tbrown.twitterStream
import spray.json._
import DefaultJsonProtocol._
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport._
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
  id_str: String,
  profile_image_url: String
)

object User extends DefaultJsonProtocol {
  implicit val UserFormat = jsonFormat12(User.apply)
}

case class Tweet (
  retweeted: Boolean,
  id: Long,
  created_at: DateTime,
  favorite_count: Int,
  text: String,
  source: String,
  retweet_count: Int,
  id_str: String,
  user: User
)

object Tweet extends DefaultJsonProtocol {
  implicit val TweetFormat = jsonFormat9(Tweet.apply)
}
