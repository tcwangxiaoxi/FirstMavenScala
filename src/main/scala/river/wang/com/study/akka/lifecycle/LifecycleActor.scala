package river.wang.com.study.akka.lifecycle

import akka.actor._
import akka.event.Logging

/**
  * Created by wxx on 2016/12/23.
  */
class LifecycleActor extends Actor {

  private val log = Logging(context.system, this)

  var child: ActorRef = _

  override def receive: Receive = {
    case num: Double => log.info(s"got a double - $num")
    case num: Int => log.info(s"got an integer - $num")
    case list: List[_] => log.info(s"got a list -  ${list.head}") //如果传入的为Nil，则会抛空指针异常
    case txt: String => child ! txt
  }

  override def preStart(): Unit = {
    log.info("about to start.")
    child = context.actorOf(Props[StringPrinter], "kiddo")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"about to restart because of $reason, during message $message")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"just restarted dut to $reason")
    super.postRestart(reason)
  }

  override def postStop(): Unit = log.info("just stopped.")

}
