package river.wang.com.study.akka.pingpong


import akka.actor._
import akka.event.Logging

/**
  * Created by wxx on 2016/12/23.
  */
class Pongy extends Actor {

  private val log = Logging(context.system, this)

  override def receive: Receive = {
    case "ping" =>
      log.info("Got a ping -- ponging back!")
      sender ! "pong"
      context.stop(self)
  }

  override def postStop(): Unit = log.info("pongy going down")
}
