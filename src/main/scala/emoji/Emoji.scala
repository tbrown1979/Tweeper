package com.tbrown.twitterStream
import scala.util.matching.Regex
import spray.json._
import DefaultJsonProtocol._

case class Emoji(unified: String) {
  override def toString: String =
    new String(unified.split("-").flatMap{ codepoint =>
      Character.toChars(Integer.parseInt(codepoint, 16))
    })
}

// object Emoji extends DefaultJsonProtocol {
//   lazy val allEmojis: List[Emoji] = getEmojis

//   def findEmojis(text: String) = {
//     Emojis(
//       allEmojis.foldLeft(List[Emoji]())((b, a) => {
//         if (text.contains(a.toString)) a :: b else b
//       })
//     )
//   }

//   private def getEmojis: List[Emoji] = {
//     //val source = scala.io.Source.fromFile("src/main/resources/emoji_pretty.json")
//     val source = getClass.getResourceAsStream("/emoji_pretty.json")
//     //val file = source.getLines mkString "\n"
//     val file = scala.io.Source.fromInputStream(source).getLines mkString "\n"
//     //source.close()
//     val emojiJson = JsonParser(file)
//     emojiJson.convertTo[List[Emoji]]
//   }

//   implicit val EmojiFormat = jsonFormat1(Emoji.apply)
//   implicit object EmojiJsonFormat extends RootJsonFormat[Emoji] {
//     def write(e: Emoji) = JsObject(
//       "emoji" -> JsString(e.toString)
//     )
//     def read(value: JsValue): Emoji = {
//       value.asJsObject.getFields("unified") match {
//         case Seq(JsString(unified)) =>
//           new Emoji(unified)
//       }
//     }
//   }
// }
