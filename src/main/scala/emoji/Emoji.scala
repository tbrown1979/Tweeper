package com.tbrown.twitterStream
import spray.json.DefaultJsonProtocol
//import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import scala.util.matching.Regex


case class Emoji(unified: String) {
  override def toString: String =
    new String(unified.split("-").flatMap{ codepoint =>
      Character.toChars(Integer.parseInt(codepoint, 16))
    })
}

object Emoji extends DefaultJsonProtocol {
  lazy val allEmojis: List[Emoji] = getEmojis

  def findEmojis(text: String) = {
    allEmojis.foldLeft(List[Emoji]())((b, a) => {
      if (text.contains(a.toString)) a :: b else b
    })
  }

  private val getEmojis: List[Emoji] = {
    val source = scala.io.Source.fromFile("src/main/scala/emoji_pretty.json")
    val file = source.getLines mkString "\n"
    source.close()
    val emojiJson = JsonParser(file)
    emojiJson.convertTo[List[Emoji]]
  }

  implicit val EmojiFormat = jsonFormat1(Emoji.apply)
}
