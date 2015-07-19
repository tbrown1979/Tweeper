package com.tbrown.twitterStream

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import org.specs2.specification.Scope
import org.joda.time.DateTime
import spray.json._
import spray.json.AdditionalFormats
import spray.httpx.SprayJsonSupport._


class ServiceSpec extends Specification with Specs2RouteTest with ServiceComponent with MemoryBasedTweetRepositoryComponent with AdditionalFormats {
  import DateTimeJsonProtocol._
  import DateTimeJsonFormat._
  val date = read(write(DateTime.now))
  val user = User("", 0, "", 0, 0, None, "", date, 0, 0, "", "", "")
  def tweet(lang: String = "", text: String = ""): Tweet =
    Tweet(false, lang, 0, None, "", date, 0, text, "", 0, "", user)

  val relevantTweet = tweet(text="Scala is great!")

  tweetRepository.store(relevantTweet)
  tweetRepository.store(tweet(text="Python is great!"))
  tweetRepository.store(tweet(text="Nothing is great!"))

  val service = new Service{}

  "Service" should {

    "return PONG when hitting PING" in {
      Get("/ping") ~> service.route ~> check {
        responseAs[String] must contain("pong")
      }
    }

    "return list of tweets from the search route" in {
      val postData = """{"size": 500, "from": 0, "searchTerms": ["Scala"]}""".asJson.asJsObject
      Post("/search", postData) ~> service.route ~> check {
        responseAs[List[Tweet]] must_== List(relevantTweet)
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> service.route ~> check {
        handled must beFalse
      }
    }
  }
}

