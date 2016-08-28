package com.tbrown.twitterStream

import twitter4j._

import fs2._
import fs2.async
import fs2.util._

object Tweets {
  import Topics._

  def stream[F[_]](implicit F: Async[F]): Stream[F, Tweet] =
    for {
      q <- Stream.eval(async.unboundedQueue[F, Tweet])
      _ <- Stream.suspend {
        val twitterStreamFilter = new TwitterStreamFactory(Util.apiConfigBuilder).getInstance

        twitterStreamFilter.addListener(Util.defaultStatusListener( e => F.unsafeRunAsync(q.enqueue1(e))(_ => ())))
        twitterStreamFilter.filter(new FilterQuery().track(topics.toArray))
        Stream.emit(())
      }
      tweet <- q.dequeue through pipe.id
    } yield tweet
}
