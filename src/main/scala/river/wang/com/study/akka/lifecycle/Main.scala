package river.wang.com.study.akka.lifecycle

import akka.actor.{ActorRef, Props}
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
object Main extends App {
  private val system = MyActorSystem.ourActorSystem
  val lifecycleActor: ActorRef = system.actorOf(Props[LifecycleActor], name = "Lifecycle")

  lifecycleActor ! math.Pi
  Thread.sleep(1000)

  lifecycleActor ! "hi there"
  lifecycleActor ! Nil
  Thread.sleep(1000)

  lifecycleActor ! "hi there3"
  Thread.sleep(1000)

  system.shutdown()
}
