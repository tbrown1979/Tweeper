package com.tbrown.twitterStream
import akka.actor._

class EmojiActor extends Actor with ActorLogging {
  def receive = {
    case emojis: Emojis => emojis.value.foreach(e => EmojiTracker.incr(e))
  }
}
