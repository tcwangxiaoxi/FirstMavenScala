package river.wang.com.study.akka.simple

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class CountdownActor extends Actor {

  val log = Logging(context.system, this)

  var n = 10

  /**
    * 永远不要像下面这样定义receive方法！！！
    */
  /*override def receive: Receive = if (n > 0) {
    case "count" =>
      log.info(s"n = $n")
      n -= 1
  } else PartialFunction.empty*/

  /**
    * 正确的方法
    */
  override def receive: Receive = counting

  private def counting: Receive = {
    case "count" =>
      log.info(s"n = $n")
      n -= 1
      if (n == 0) context.become(done)
  }

  private def done: Receive = PartialFunction.empty

}

object CountdownActor extends App {

  private val system = MyActorSystem.ourActorSystem
  val countdownActor: ActorRef = system.actorOf(Props[CountdownActor], name = "deafy")

  for (num <- 0 to 14) {
    countdownActor ! "count"
  }
  Thread.sleep(1000)

  system.shutdown()

}
