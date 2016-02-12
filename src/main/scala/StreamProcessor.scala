package com.tbrown.twitterStream
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

trait StreamProcessor
    extends TweetStreamComponent
    with TweetRepositoryComponent
{
  import TweetMetrics._

  def relayTweet(t: Tweet): Task[Unit] = {
    incrTweetCount
    tweetRepository.store(t)

  }

  stream.subscribe.to()
}

