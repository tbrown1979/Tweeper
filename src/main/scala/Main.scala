package com.tbrown.twitterStream
import java.util.concurrent.TimeUnit;

import akka.actor.{Actor, ActorSystem, ActorRef}
import akka.routing.RoundRobinRouter
import akka.actor.Props
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import twitter4j._
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.index.IndexResponse


object Main extends App {
  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  val storage = StreamingActorSystem.actorOf(Props[TweetStatStorage])

  twitterStream.addListener(Util.simpleStatusListener(storage))
  twitterStream.sample
  // Thread.sleep(5000)
  //system.scheduler.schedule(0 seconds, 5 seconds, storage, Report)

}


object TestMain extends App {
  // scala uses the java driver which listens on port 9300
  val client = ElasticClient.local

  val res: Future[SearchResponse] = client execute { search in "bands"->"singers" }

  //val res: Future[IndexResponse] = client execute { index into "bands/singers" fields "name"->"chris martin" }


  res onComplete{
    case Success(s) => println(s)
    case Failure(t) => println("An error has occured: " + t)
  }

  println("Request sent")

  //adjust this if needed
  Thread.sleep(1000)
}
