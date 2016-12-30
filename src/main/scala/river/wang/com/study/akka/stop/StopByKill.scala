package river.wang.com.study.akka.stop

import akka.actor._

/**
  * Created by wxx on 2016/12/30.
  */
object StopByKill extends App {

  val system = ActorSystem("DeathWatchDemo")
  val parentActor = system.actorOf(Props[Parent], name = "Parent")

  // look up jason, then kill it
  println("kill the child actor")
  val jasonActor = system.actorSelection("/user/Parent/Jason")

  //  for (_ <- 0 until 5) {
  jasonActor ! "123"
  //  }

  // TODO: Kill 和 PoisonPill 都是停止Actor 没有具体看出区别
  jasonActor ! Kill
  //  jasonActor ! PoisonPill
  Thread.sleep(5000)


  //  println("calling system.shutdown")
  //  system.shutdown
}

import akka.actor._

class Jason extends Actor {
  def receive = {
    case _ =>
      Thread.sleep(5000)
      println("jason got a message")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    println("preRestart")
  }

  override def preStart(): Unit = {
    super.preStart()
    println("preStart")
  }
}

class Parent extends Actor {
  // start Jason as a child, then keep an eye on it
  val jason = context.actorOf(Props[Jason], name = "Jason")
  context.watch(jason)

  def receive = {
    case Terminated(`jason`) => println("OMG, they killed jason")
    case _ => println("parent received a message")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    println("Parent preRestart")
  }

  override def preStart(): Unit = {
    super.preStart()
    println("Parent preStart")
  }

}



