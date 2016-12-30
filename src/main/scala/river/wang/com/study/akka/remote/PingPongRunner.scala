package river.wang.com.study.akka.remote

import akka.actor.{Actor, ActorIdentity, Identify, Props}
import akka.event.Logging
import river.wang.com.study.akka.pingpong.Pingy

/**
  * Created by wxx on 2016/12/30.
  */
class PingPongRunner extends Actor {

  val log = Logging(context.system, this)

  private val pingy = context.actorOf(Props[Pingy], "Pingy")

  override def receive: Receive = {
    case "start" =>
      val pongySys = "akka.tcp://PongyDimension@127.0.0.1:24321"
      val pongyPath = "/user/pongy"
      val selection = context.actorSelection(pongySys + pongyPath)
      selection ! Identify(0)
    case ActorIdentity(0, Some(ref)) =>
      pingy ! ref
    case ActorIdentity(0, None) =>
      log.info("Something's wrong - ain't no pongy anywhere!")
      context.stop(self)
    case "pong" =>
      log.info("got a pong from another dimension.")
      context.stop(self)
  }
}
