package com.tbrown
import spray.json.DefaultJsonProtocol
//import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import scala.util.matching.Regex


object Emojis {
  val emojis: List[Emoji] = getEmojis

  def findEmojis(text: String) = {
    emojis.foldLeft(List[Emoji]())((b, a) => {
      if (text.contains(a.toString)) a :: b else b
    })
  }

  private def getEmojis: List[Emoji] = {
    val source = scala.io.Source.fromFile("src/main/scala/emoji_pretty.json")
    val file = source.getLines mkString "\n"
    source.close()
    val emojiJson = JsonParser(file)
    emojiJson.convertTo[List[Emoji]]
  }
}
