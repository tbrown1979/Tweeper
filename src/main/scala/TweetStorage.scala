package com.tbrown

import akka.actor.Props
import akka.actor.{Actor, ActorSystem, ActorRef}
import akka.pattern.{ ask, pipe }
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import concurrent.ExecutionContext
import concurrent.Future
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.util.{Success, Failure}
import twitter4j._

class TweetStatStorage extends Actor {
  import context.dispatcher

  val startTime = currentTime
  var numOfTweets = 0D
  var tweetsWithUrls = 0D 
  var tweetsWithEmojis = 0D
  var tweetsWithPhotos = 0D
  val emojiStorage = new ElementCounter[Emoji]{}
  val hashtagStorage = new ElementCounter[String]{}
  val domainStorage = new ElementCounter[String]{}

  def avgPerSecond = (numOfTweets / (currentTime - startTime)).round
  def avgPerMinute = avgPerSecond * 60
  def avgPerHour   = avgPerMinute * 60
  def track(implicit stats: Tweet) = {
    incrTweets
    updateUrlCount(stats)
    updateEmojiCount(stats)
    updatePhotoCount(stats)
    updateHashtags(stats)
    updateEmojis(stats)
    updateDomains(stats)
  }

  def receive = {
    case tweet: Tweet => track(tweet)
    case Report => report
  }

  private def currentTime = (System.currentTimeMillis / 1000.0)
  private def incrTweets = numOfTweets += 1
  private def updateUrlCount(t: Tweet)   = if (t.hasUrl)    tweetsWithUrls += 1
  private def updateEmojiCount(t: Tweet) = if (t.hasEmojis) tweetsWithEmojis += 1
  private def updatePhotoCount(t: Tweet) = if (t.hasPhotos) tweetsWithPhotos += 1
  private def updateEmojis(t: Tweet) = t.emojis.foreach(e => emojiStorage.incrElementCount(e))
  private def updateHashtags(t: Tweet) = t.hashtags.foreach(e => hashtagStorage.incrElementCount("#" + e.getText))
  private def updateDomains(t: Tweet) = t.domains.foreach(e => domainStorage.incrElementCount(e))

  private def percentTweetsWith(x: Double) = ((x / numOfTweets) * 100).round
  private def report = Future {
    s"Number of Total Tweets: $numOfTweets  \n" +
    s"Avg Tweets Per Second:  $avgPerSecond \n" +
    s"Avg Tweets Per Min:     $avgPerMinute \n" + 
    s"Avg Tweets Per Hour:    $avgPerHour   \n" +
    s"% With Photos:          ${percentTweetsWith(tweetsWithPhotos)}% \n" +
    s"% With Urls:            ${percentTweetsWith(tweetsWithUrls)}%   \n" +
    s"% with Emojis:          ${percentTweetsWith(tweetsWithEmojis)}% \n" +
    s"Top Emojis:             ${emojiStorage.topElements(3).mkString("  ")} \n" +
    s"Top Hashtags:           ${hashtagStorage.topElements(3).mkString(", ")} \n" +
    s"Top domains:            ${domainStorage.topElements(3).mkString(", ")}"
  }(ExecutionContext.fromExecutor(null)) onComplete {
    case Success(s) => println(s)
    case Failure(e) => println("Failed: " + e)
  }
}

trait ElementCounter[A] {
  val map = collection.mutable.Map[A, Int]()

  def topElements(n: Int = 3): List[A] =
    map.toList.foldLeft(List[(A, Int)]())( (b, a) =>
      if (b.size < n) a :: b
      else {
        val min = b.map(_._2).min
        val b_ = b.sortWith( _._2 < _._2 )
        if (a._2 > min) a :: b_.drop(1)
        else b
      }
    ).sortWith(_._2 > _._2).map(_._1)

  def incrElementCount(elem: A) = map.update(elem, map.getOrElse(elem, 0) + 1)


}
