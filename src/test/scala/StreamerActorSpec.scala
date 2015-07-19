package com.tbrown.twitterStream

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.{ DefaultTimeout, ImplicitSender, TestKit }
import scala.concurrent.duration._
import scala.collection.immutable
import akka.testkit.TestActorRef
import org.specs2.specification.Scope
import org.specs2.mutable.Specification
//import org.specs2.execute._
import org.joda.time.DateTime
import akka.testkit.TestProbe
import spray.can.Http.RegisterChunkHandler
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.reflect._
import spray.can.Http
import spray.can.Http.RegisterChunkHandler
import spray.can.server.Stats
import spray.http._
import spray.util._
import spray.json._
import MediaTypes._
import spray.routing.directives.RespondWithDirectives._
import spray.http.ContentTypes._
import HttpHeaders.{`Cache-Control`, `Connection`}
import CacheDirectives.`no-cache`


trait DeactivatedTimeConversions extends org.specs2.time.TimeConversions {
  override def intToRichLong(v: Int) = super.intToRichLong(v)
}

object StreamerSpec extends Specification with DeactivatedTimeConversions {

  class Actors extends TestKit(ActorSystem("test")) with Scope {
    import EventSourceService._
    val probe = TestProbe()
    val deadProbe = TestProbe()

    val eventStream = system.eventStream

    val user = User("", 0, "", 0, 0, None, "", DateTime.now, 0, 0, "", "", "")
    def tweet(lang: String = "", text: String = ""): Tweet = 
      Tweet(false, lang, 0, None, "", DateTime.now, 0, text, "", 0, "", user)
  }

  "Streamer" should {

    "retrieve tweets from the EventStream and send them to a client actor with Streamer" in new Actors {
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

    "retrieve tweets from the EventStream and send them to a client actor with Streamer" in new Actors {

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

    "filter irrelevant tweets when streaming tweets with TweetStreamer" in new Actors {
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

    "match language correctly with TweetStreamer" in new Actors {
      val testTweetStreamer = TestActorRef(new TweetStreamer(deadProbe.ref, Nil, Some("en")))
      val testTweetActor = testTweetStreamer.underlyingActor

      testTweetActor.matchLang(tweet(lang="en")) must_== true
    }

    "determine if term is valid" in new Actors {
      val testTweetStreamer = TestActorRef(new TweetStreamer(deadProbe.ref, List("asdf")))
      val testTweetActor = testTweetStreamer.underlyingActor

      testTweetActor.tweetHasMatchedTerms(tweet(text="asdf")) must_== true
    }
  }
}



