package river.wang.com.study.akka.parentchild

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class ParentActor extends Actor {
  val log = Logging(context.system, this)

  override def receive = {
    case "create" =>
      context.actorOf(Props[ChildActor])
      log.info(s"create a kid; children = $context.children")
    case "sayhi" =>
      log.info("Kids, say hi!")
      for (c <- context.children) c ! "sayhi"
    case "stop" =>
      log.info("parent stopping")
      context.stop(self)
  }

  override def postStop(): Unit = {
    log.info("child stopped!")
  }
}

object ParentActor extends App {

  private val system = MyActorSystem.ourActorSystem
  val parentActor: ActorRef = system.actorOf(Props[ParentActor], name = "parent")

  parentActor ! "create"
  parentActor ! "create"
  Thread.sleep(1000)

  parentActor ! "sayhi"
  Thread.sleep(1000)

  parentActor ! "stop"
  Thread.sleep(1000)

  parentActor ! "create"
  Thread.sleep(1000)

  system.shutdown()

}
