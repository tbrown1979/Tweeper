package com.tbrown.twitterStream
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

trait StreamProcessor extends TweetStreamComponent with TweetRepositoryComponent {

  object streamProcessor {
    import TweetMetrics._

    def relayTweet(t: Tweet): Task[Unit] = {
      incrTweetCount
      tweetRepository.store(t)
      Task.delay(Unit)
    }

    lazy val subStream = stream.subscribe.to(Process.constant(relayTweet _))
  }
}

