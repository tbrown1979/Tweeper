package com.tbrown.twitterStream

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.TestActorRef
import akka.testkit.TestProbe
import akka.testkit.{ DefaultTimeout, ImplicitSender, TestKit }
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.collection.immutable
import scala.concurrent.duration._
import spray.can.Http
import spray.can.Http.RegisterChunkHandler
import spray.can.server.Stats
import spray.http._
import spray.json._

trait DeactivatedTimeConversions extends org.specs2.time.TimeConversions {
  override def intToRichLong(v: Int) = super.intToRichLong(v)
}

object StreamerSpec extends Specification with DeactivatedTimeConversions {

  class context extends TestKit(ActorSystem("test")) with Scope {
    import EventSourceService._
    val probe = TestProbe()
    val deadProbe = TestProbe()

    val eventStream = system.eventStream

    val date = DateTime.now
    val user = User("", 0, "", 0, 0, None, "", DateTime.now, 0, 0, "", "", "")
    def tweet(lang: String = "", text: String = ""): Tweet = 
      Tweet(false, lang, 0, None, "", date, 0, text, "", 0, "", user)
  }

  "Streamer" should {

    "retrieve tweets from the EventStream and send them to a client actor with Streamer" in new context {
      import EventSourceService._

      val testStreamer = TestActorRef(new Streamer[Tweet](probe.ref))
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())

      probe.ignoreMsg {
        case c: ChunkedResponseStart => true
      }
      val probedMsgs = probe.receiveN(5, 1000 millis)

      probedMsgs.size must_== 5

      probedMsgs.map( _ match {
        case c: ChunkedResponseStart => true
        case m: MessageChunk => MessageChunk(formatAsSSE(tweet().toJson.toString)) must_== m
        case _ => ko
      })
    }

    "retrieve tweets from the EventStream and send them to a client actor with Streamer" in new context {

      import EventSourceService._

      val testStreamer = TestActorRef(new TweetStreamer(probe.ref))
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())

      probe.ignoreMsg {
        case c: ChunkedResponseStart => true
      }
      val probedMsgs = probe.receiveN(5, 1000 millis)

      probedMsgs.size must_== 5

      probedMsgs.map( _ match {
        case c: ChunkedResponseStart => true
        case m: MessageChunk => MessageChunk(formatAsSSE(tweet().toJson.toString)) must_== m
        case _ => ko
      })
    }

    "filter irrelevant tweets when streaming tweets with TweetStreamer" in new context {
      import EventSourceService._
      val relevantTweet = tweet(text="valid")

      val testStreamer = TestActorRef(new TweetStreamer(probe.ref, terms=List("valid")))
      eventStream.publish(relevantTweet)
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())
      eventStream.publish(tweet())

      probe.ignoreMsg {
        case c: ChunkedResponseStart => true
      }
      val probedMsgs = probe.receiveN(1, 1000 millis)
      probedMsgs.size must_== 1

      probedMsgs.map( _ match {
        case c: ChunkedResponseStart => true
        case m: MessageChunk => MessageChunk(formatAsSSE(relevantTweet.toJson.toString)) must_== m
        case _ => ko
      })
      
    }

    "match language correctly with TweetStreamer" in new context {
      val testTweetStreamer = TestActorRef(new TweetStreamer(deadProbe.ref, Nil, Some("en")))
      val testTweetActor = testTweetStreamer.underlyingActor

      testTweetActor.matchLang(tweet(lang="en")) must_== true
    }

    "determine if term is valid" in new context {
      val testTweetStreamer = TestActorRef(new TweetStreamer(deadProbe.ref, List("asdf")))
      val testTweetActor = testTweetStreamer.underlyingActor

      testTweetActor.tweetHasMatchedTerms(tweet(text="asdf")) must_== true
    }
  }
}



