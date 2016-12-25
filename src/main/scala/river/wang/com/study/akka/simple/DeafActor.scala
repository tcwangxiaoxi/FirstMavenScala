package river.wang.com.study.akka.simple

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class DeafActor extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = PartialFunction.empty

  override def unhandled(msg: Any): Unit = msg match {
    case msg: String => log.info(s"I dont hear '$msg'")
    case message => super.unhandled(message)
  }
}

object DeafActor extends App {

  private val system = MyActorSystem.ourActorSystem
  val deafActor: ActorRef = system.actorOf(Props[DeafActor], name = "deafy")

  deafActor ! "hi"
  Thread.sleep(1000)

  deafActor ! 123
  Thread.sleep(1000)

  system.shutdown()

}
