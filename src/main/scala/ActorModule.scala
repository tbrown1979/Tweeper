package com.tbrown.twitterStream

import akka.actor._
import akka.actor.{ActorContext, Actor}
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

trait ActorModule {
  def system: ActorSystem
  def actorOf(props: Props, name: String): ActorRef = {
    system.actorOf(props, name)
  }
}

trait TweeperActorModule {
  implicit val system: ActorSystem = StreamingActorSystem
}
