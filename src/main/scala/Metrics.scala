package com.tbrown.twitterStream
import com.codahale.metrics._

object TweetMetrics {
  private val metrics = new MetricRegistry

  private val tweetsConsumed: Counter = metrics.counter(MetricRegistry.name("tweets.count"))
  def incrTweetCount = {
    tweetsConsumed.inc
    println(s"Tweet Count: $getTweetCount")
    println(s"Mean Tweets: $getMeanRate")
  }

  def getTweetCount = tweetsConsumed.getCount

  private val tweetsRate: Meter = metrics.meter(MetricRegistry.name("tweets.rate"))
  def markTweet = tweetsRate.mark
  def getMeanRate = tweetsRate.getMeanRate
  def getOneMinuteRate = tweetsRate.getOneMinuteRate
  def getFiveMinuteRate = tweetsRate.getFiveMinuteRate
}
