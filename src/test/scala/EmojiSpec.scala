
package com.tbrown.twitterStream

import org.specs2.specification.Scope
import org.specs2.mutable.Specification
import org.specs2.execute._
import scala.concurrent._
import scala.concurrent.duration._

object ScraperSpec extends Specification {
  import Emoji._
  "Emoji object" should {

    // "return a list of emojis found in string" in {
    //   val s = "RT @woahirwn: rt for a long indirect 🌿😘😅12345 🇹 🇨🇳"
    //   val st = "😅"
    //   val t = "1f1e8-1f1f3"
    //   def test(x: String) =
    //     new String(x.split("-").flatMap{ codepoint =>
    //       Character.toChars(Integer.parseInt(codepoint, 16))
    //     })
    //   println(s)
    //   println(test(t))
    //   println(s.contains("🇨🇳"))
    //   findEmojis(s).toString === List("🇨🇳", "😘", "😅", "🌿" ).toString
    // }
    
  }
}
