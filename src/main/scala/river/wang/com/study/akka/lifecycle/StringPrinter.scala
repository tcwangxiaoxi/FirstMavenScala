package river.wang.com.study.akka.lifecycle

import akka.actor._
import akka.event.Logging

/**
  * Created by wxx on 2016/12/23.
  */
class StringPrinter extends Actor {

  private val log = Logging(context.system, this)

  override def receive: Receive = {
    case msg =>
      Thread.sleep(500)
      log.info(s"printer got message $msg")
  }

  override def preStart(): Unit = log.info("printer preStart.")

  override def postStop(): Unit = log.info("printer postStop.")

}
