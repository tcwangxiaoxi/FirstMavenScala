package river.wang.com.study.akka.pingpong

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/29.
  */
class PingPongMaster extends Actor {

  private val log = Logging(context.system, this)

  private val system = MyActorSystem.ourActorSystem
  private val pingy = system.actorOf(Props[Pingy], "pingy")
  private val pongy = system.actorOf(Props[Pongy], "pongy")

  override def receive: Receive = {
    case "start" => pingy ! pongy
    case "pong" => log.info("master finish")
  }

  override def postStop(): Unit = {
    log.info("master going down")
  }
}

object PingPongMaster extends App {

  private val system = MyActorSystem.ourActorSystem

  private val masta = system.actorOf(Props[PingPongMaster], "master")
  for (i <- 1 to 10) {
    masta ! "start"
  }

}

