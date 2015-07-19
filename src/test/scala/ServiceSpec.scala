package com.tbrown.twitterStream

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with ServiceComponent with MemoryBasedTweetRepositoryComponent {
  val service = new Service{}
  
  "Service" should {

    "return PONG when hitting PING" in {
      Get("/ping") ~> service.route ~> check {
        responseAs[String] must contain("PONG!")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> service.route ~> check {
        handled must beFalse
      }
    }

    // "return a MethodNotAllowed error for PUT requests to the root path" in {
    //   Put() ~> sealRoute(myRoute) ~> check {
    //     status === MethodNotAllowed
    //     responseAs[String] === "HTTP method not allowed, supported methods: GET"
    //   }
    // }
  }
}
