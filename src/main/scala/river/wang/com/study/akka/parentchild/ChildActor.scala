package river.wang.com.study.akka.parentchild

import akka.actor._
import akka.event.Logging

/**
  * Created by wxx on 2016/12/23.
  */
class ChildActor extends Actor {

  val log = Logging(context.system, this)

  override def receive = {
    case "sayhi" =>
      val parent = context.parent
      log.info(s"my parent $parent made me say hi")
  }

  override def postStop(): Unit = {
    log.info("child stopped!")
  }
}