package com.tbrown
import akka.actor.{Actor, ActorRef}
import twitter4j._

class Tweet(status: Status) {
  val text = status.getText
  val urlEntities = status.getURLEntities
  val expandedUrls = urlEntities.map(_.getExpandedURL)

  val emojis = Emojis.findEmojis(text)
  val domains: List[String] = urlEntities.map(_.getDisplayURL.takeWhile(_ != '/')).toList
  val hasUrl: Boolean = !urlEntities.isEmpty
  val hasPhotos: Boolean = expandedUrls.foldLeft(false)((b, a) => isPhoto(a) || b)
  val hasEmojis = !emojis.isEmpty
  val hashtags = status.getHashtagEntities.toList
  val urlDomains = status.getURLEntities.toList

  def isPhoto(t: String) = t.contains("pic.twitter.com") || t.contains("instagram")
}
