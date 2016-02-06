package com.tbrown.twitterStream
import com.codahale.metrics._
import java.util.concurrent.TimeUnit
//import org.elasticsearch.metrics.ElasticsearchReporter
import scala.concurrent.duration.TimeUnit

trait MetricReporting {
  def metrics: MetricRegistry
  def reporter: ScheduledReporter
}

trait ConsoleMetricReporting extends MetricReporting {
  val reporter = ConsoleReporter.forRegistry(metrics)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()
}

object TweetMetrics extends ConsoleMetricReporting {
  reporter.start(5, TimeUnit.SECONDS);

  val metrics = new MetricRegistry

  val sampleTweetsConsumed: Counter = metrics.counter(MetricRegistry.name("sample.tweets.count"))
  val filterTweetsConsumed: Counter = metrics.counter(MetricRegistry.name("filter.tweets.count"))
  def incrTweetCount(t: TweetStreams.Value) = t match {
    case TweetStreams.Filter => filterTweetsConsumed.inc
    case TweetStreams.Sample => sampleTweetsConsumed.inc
  }

  def sampleTweetCount = sampleTweetsConsumed.getCount
  def filterTweetCount = filterTweetsConsumed.getCount

  val sampleTweetsRate: Meter = metrics.meter(MetricRegistry.name("sample.tweets.rate"))
  val filterTweetsRate: Meter = metrics.meter(MetricRegistry.name("filter.tweets.rate"))
  def markTweet(t: TweetStreams.Value) = t match {
    case TweetStreams.Filter => filterTweetsConsumed.inc
    case TweetStreams.Sample => sampleTweetsConsumed.inc
  }

  def meanSampleRate = sampleTweetsRate.getMeanRate
  def oneMinuteSampleRate = sampleTweetsRate.getOneMinuteRate
  def fiveMinuteSampleRate = sampleTweetsRate.getFiveMinuteRate

  def meanFilterRate = filterTweetsRate.getMeanRate
  def oneMinuteFilterRate = filterTweetsRate.getOneMinuteRate
  def fiveMinuteFilterRate = filterTweetsRate.getFiveMinuteRate

  def sampleStats: StreamStats = StreamStats(oneMinuteSampleRate, sampleTweetCount)
  def filterStats: StreamStats = StreamStats(oneMinuteFilterRate, filterTweetCount)
}

