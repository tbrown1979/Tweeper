package com.tbrown.twitterStream
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

trait TweetStreamComponent {
  val stream: TweetStream

  trait TweetStream {
    def subscribe: Process[Task, Tweet]
    def publishOne(t: Tweet): Unit
    def publish: Sink[Task, Tweet]
  }
}

trait DefaultTweetStreamComponent
    extends TweetStreamComponent
    with MemoryBasedTweetRepositoryComponent {
  val stream = new DefaultTweetStream {}

  trait DefaultTweetStream extends TweetStream {
    val topic = async.topic[Tweet]()

    def subscribe = topic.subscribe
    def publishOne(t: Tweet) = topic.publishOne(t).runAsync(_ => Unit)
    def publish = topic.publish
  }
}
