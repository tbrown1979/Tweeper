package com.tbrown.twitterStream

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.websocket._
import org.http4s.websocket.WebsocketBits._

import scalaz.concurrent.Task

import scalaz.stream.Exchange
import scalaz.stream.{Process, Sink}

trait Routes {
  import JsonModule._

  object websocketRoutes {
    lazy val service = HttpService {
      case GET -> Root / "ping" =>
        Ok("pong")

//      case req@ GET -> Root / "ws" =>
//        val src = stream.subscribe.map(t => Text(toJsonStr(t)))
//        val sink: Sink[Task, WebSocketFrame] = Process.constant {
//          case Text(t, _) => Task.delay(Unit)
//          case f       => Task.delay(println(s"Unknown type: $f"))
//        }
//        WS(Exchange(src, sink))
    }
  }
}
