package com.tbrown.twitterStream

import java.util.concurrent.Executors
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.websocket._
import org.http4s.websocket.WebsocketBits._
import org.log4s.getLogger
import scala.concurrent.duration._
import scala.util.Properties.envOrNone
import scalaz._
import Scalaz._
import scalaz.concurrent.Strategy
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.async.unboundedQueue
import scalaz.stream.time.awakeEvery
import scalaz.stream.{DefaultScheduler, Exchange}
import scalaz.stream.{Process, Sink}

trait Routes extends TweetStreamComponent with JsonModule {

  object websocketRoutes {
    lazy val service = HttpService {
      case GET -> Root / "ping" =>
        Ok("pong")

      case req@ GET -> Root / "ws" =>
        val src = stream.subscribe.map(t => Text(toJsonStr(t)))
        val sink: Sink[Task, WebSocketFrame] = Process.constant {
          case Text(t, _) => Task.delay(Unit)
          case f       => Task.delay(println(s"Unknown type: $f"))
        }
        WS(Exchange(src, sink))
    }
  }
}
