package river.wang.com.study.akka.simple

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class CheckActor extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case path: String =>
      log.info(s"checking path $path")
      context.actorSelection(path) ! Identify(path)
    case ActorIdentity(path, Some(ref)) =>
      log.info(s"found actor $ref at $path")
    case ActorIdentity(path, None) =>
      log.info(s"could not find an actor at $path")
  }
}

object CheckActor extends App {

  private val system = MyActorSystem.ourActorSystem
  val checkActor: ActorRef = system.actorOf(Props[CheckActor], name = "check")

  checkActor ! "../*"
  Thread.sleep(1000)

  checkActor ! "../../*"
  Thread.sleep(1000)

  checkActor ! "/system/*"
  Thread.sleep(1000)

  checkActor ! "/user/checker2"
  Thread.sleep(1000)

  system.shutdown()
}
