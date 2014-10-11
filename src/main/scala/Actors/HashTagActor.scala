package com.tbrown.twitterStream
import akka.actor._

class HashtagActor extends Actor with ActorLogging {
  def receive = {
    case hts: Hashtags => hts.value.foreach(h => HashtagTracker.incrElementCount(h))
  }
}
