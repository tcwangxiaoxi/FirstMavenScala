package river.wang.com.study.akka.pingpong

import akka.actor._
import akka.event.Logging
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by wxx on 2016/12/23.
  */
class Pingy extends Actor {

  private val log = Logging(context.system, this)

  override def receive: Receive = {
    case pongyRef: ActorRef =>
      implicit val timeout = Timeout(2 second)
      val f = pongyRef ? "ping"
      // TODO: 需要看书252页，了解pipeTo方法的原理
      f.pipeTo(sender)
  }


  override def postStop(): Unit = log.info("pongy going down")
}
