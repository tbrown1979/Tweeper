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
import twitter4j._

trait RoutesComponent extends TweetStreamComponent with JsonModule {
  lazy val service = new Routes{}.service

  trait Routes {
    lazy val service = HttpService {
      case GET -> Root / "ping" =>
        Ok("pong")

      case req@ GET -> Root / "ws" =>
        val src = stream.subscribe.map(t => Text(toJsonStr(t)))
        val sink: Sink[Task, WebSocketFrame] = Process.constant {
          case Text(t, _) => Task.delay(println(t))
          case f       => Task.delay(println(s"Unknown type: $f"))
        }
        WS(Exchange(src, sink))
    }
  }
}

class Boot(host: String, port: Int) extends TopicsConfig
  with MemoryBasedTweetRepositoryComponent
    with TweetStreamListeners
    with DefaultTweetStreamComponent
    with StreamProcessor
    with RoutesComponent {

  private val logger = getLogger
  private val pool = Executors.newCachedThreadPool()

  logger.info(s"Starting Http4s-blaze example on '$host:$port'")

  val twitterStreamFilter = new TwitterStreamFactory(Util.apiConfigBuilder).getInstance

  twitterStreamFilter.addListener(filterStatusListener)

  twitterStreamFilter.filter(new FilterQuery().track(topics.toArray))

  def write(t: Tweet): Task[Unit] = Task.delay(println("writing a tweet...\n"))

  startTweetCapture

  stream.subscribe.to(Process.constant(write _)).run.runAsync(_ => Unit)

  //Construct the blaze pipeline.
  def build: ServerBuilder =
    BlazeBuilder.bindHttp(port, host)
      .withWebSockets(true)
      .mountService(new Routes{}.service)
      .withServiceExecutor(pool)
}

object Boot extends App {
  val ip =   "0.0.0.0"
  val port = envOrNone("HTTP_PORT") map(_.toInt) getOrElse(8080)

   new Boot(ip, port)
     .build
     .run
     .awaitShutdown()
}
