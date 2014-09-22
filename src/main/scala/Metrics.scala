package com.tbrown.twitterStream
import com.codahale.metrics._
import org.elasticsearch.metrics.ElasticsearchReporter
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.TimeUnit

object TweetMetrics {
  val metrics = new MetricRegistry
  val reporter = ElasticsearchReporter.forRegistry(metrics)
    .hosts("localhost:9200")
    .build();
  reporter.start(5, TimeUnit.SECONDS);

  val tweetsConsumed: Counter = metrics.counter(MetricRegistry.name("tweets.count"))
  def incrTweetCount = tweetsConsumed.inc
  def getTweetCount = tweetsConsumed.getCount

  val tweetsRate: Meter = metrics.meter(MetricRegistry.name("tweets.rate"))
  def markTweet = tweetsRate.mark
  def getMeanRate = tweetsRate.getMeanRate
  def getOneMinuteRate = tweetsRate.getOneMinuteRate
  def getFiveMinuteRate = tweetsRate.getFiveMinuteRate

  def stats: TweetStreamStats = TweetStreamStats(getMeanRate, getTweetCount)
}
