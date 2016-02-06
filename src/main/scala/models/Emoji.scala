package com.tbrown.twitterStream
import scala.util.matching.Regex
import jsonz._

case class Emoji(unified: String) {
  override def toString: String =
    new String(unified.split("-").flatMap{ codepoint =>
      Character.toChars(Integer.parseInt(codepoint, 16))
    })
}

object Emoji extends JsonModule {
  import Jsonz._
  lazy val allEmojis: List[Emoji] = getEmojis

  def findEmojis(text: String) =
    allEmojis.foldLeft(List[Emoji]())((b, a) => {
      if (text.contains(a.toString)) a :: b else b
    })

  //implicit val EmojiFormat = productFormat1(Emoji.apply)(Emoji.unapply)
  implicit object EmojiJsonFormat extends Format[Emoji] {
    import jsonz.Fields._

    def writes(e: Emoji) = 
      JsObject(
        "emoji" -> toJson(e.unified) ::
          Nil
      )

    def reads(js: JsValue) = {
      val unified = field[String]("unified", js)
      unified.map(Emoji(_))
    }
  }

  private def getEmojis: List[Emoji] = {
    //val source = scala.io.Source.fromFile("src/main/resources/emoji_pretty.json")
    val source = getClass.getResourceAsStream("/emoji_pretty.json")
    //val file = source.getLines mkString "\n"
    val file = scala.io.Source.fromInputStream(source).getLines mkString "\n"
    //source.close()
    //val emojiJson = JsonParser(file)
    fromJsonStr[List[Emoji]](file).getOrElse(List.empty)
  }

}
