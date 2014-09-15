package com.tbrown.twitterStream
import spray.json.DefaultJsonProtocol
//import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import scala.util.matching.Regex
import twitter4j._

case object Report
case class TweetFound(status: Status)

case class Emoji(
  name:         String,
  unified:      String,
  variations:   List[String],
  docomo:       Option[String],
  au:           Option[String],
  softbank:     Option[String],
  google:       Option[String],
  image:        String,
  sheet_x:      Int,
  sheet_y:      Int,
  short_name:   String,
  short_names:  List[String],
  text:         Option[String],
  apple_img:    Boolean,
  hangouts_img: Boolean,
  twitter_img:  Boolean
) {
  override def toString: String =
    new String(unified.split("-").flatMap{ codepoint =>
      Character.toChars(Integer.parseInt(codepoint, 16))
    })
}

object Emoji extends DefaultJsonProtocol {
  implicit val EmojiFormat = jsonFormat16(Emoji.apply)
}

case class Tweet(
  retweeted: Boolean,
  id: Long
)
