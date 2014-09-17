package com.tbrown.twitterStream
import com.codahale.metrics._

object TweetMetrics {
  val metrics = new MetricRegistry

  val tweetsConsumed: Counter = metrics.counter(MetricRegistry.name("tweets.count"))
  def incrTweetCount = tweetsConsumed.inc
  def getTweetCount = tweetsConsumed.getCount

  val tweetsRate: Meter = metrics.meter(MetricRegistry.name("tweets.rate"))
  def markTweet = tweetsRate.mark
  def getMeanRate = tweetsRate.getMeanRate
  def getOneMinuteRate = tweetsRate.getOneMinuteRate
  def getFiveMinuteRate = tweetsRate.getFiveMinuteRate
}
