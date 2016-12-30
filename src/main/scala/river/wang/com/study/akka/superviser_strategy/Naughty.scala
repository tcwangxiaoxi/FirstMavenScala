package river.wang.com.study.akka.superviser_strategy

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor._

/**
  * Created by wxx on 2016/12/30.
  */
class Naughty extends Actor {

  override def receive: Receive = {
    case s: String => println(s)
    case msg => throw new RuntimeException
  }

  override def postRestart(reason: Throwable): Unit = {
    println("naughty restarted!")
  }
}

class Supervisor extends Actor {
  private val child1 = context.actorOf(Props[Naughty], "Naughty1")
  private val child2 = context.actorOf(Props[Naughty], "Naughty2")

  override def receive: Receive = PartialFunction.empty

  /**
    * 父Actor对象可以选择执行下列操作：
    *
    * 通过配置Restart值，重启子Actor对象
    * 通过配置Resume值，在不重启子Actor对象的情况下恢复子Actor对象
    * 通过配置Stop值，永久停止Actor对象
    * 通过配置Escalate值，使用同一个异常使本身也失效
    *
    * =====================================================================
    * 监督策略：
    *
    * OneForOneStrategy 表示：
    * 当Actor对象失效时，根据导致该对象失效的异常，该对象会被恢复、重启或停止
    *
    * AllForOneStrategy 表示：
    * 当多个子Actor对象中的某一个失效时，其他子Actor对象都会被恢复、重启或停止
    *
    */
  /*override val supervisorStrategy = OneForOneStrategy() {
    case ake: ActorKilledException => Restart
    case _ => Escalate
  }*/
  override val supervisorStrategy = AllForOneStrategy() {
    case ake: ActorKilledException => Restart
    case _ => Escalate
  }
}

object Naughty extends App {

  val system = ActorSystem("NaughtyDemo")
  val parentActor = system.actorOf(Props[Supervisor], name = "Supervisor")


  def testOne2One() {
    system.actorSelection("/user/Supervisor/*") ! Kill
    system.actorSelection("/user/Supervisor/*") ! "sorry about that"
    system.actorSelection("/user/Supervisor/*") ! "kaboom".toList
  }

  def testAll2One(): Unit = {
    system.actorSelection("/user/Supervisor/Naughty1") ! Kill
    system.actorSelection("/user/Supervisor/*") ! "sorry about that"
    system.actorSelection("/user/Supervisor/*") ! "kaboom".toList
  }

//  testOne2One()
  testAll2One()

}


