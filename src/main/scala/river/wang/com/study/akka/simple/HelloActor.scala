package river.wang.com.study.akka.simple

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class HelloActor(val hello: String) extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case `hello` => log.info(s"Received a `$hello` ... $hello!")
    case msg => log.info(s"Unexpected message '$msg'")
      context.stop(self)
  }
}

object HelloActor {
  // 不建议使用，该方法可能会导致闭包引用外部类的问题
  def props(hello: String) = Props(new HelloActor(hello))

  def propsAlt(hello: String) = Props(classOf[HelloActor], hello)
}

object ActorsCreate extends App {

  private val system = MyActorSystem.ourActorSystem

  val hiActor: ActorRef = system.actorOf(HelloActor.propsAlt("hi"), name = "greeter")

  hiActor ! "hi"

  Thread.sleep(1000)

  hiActor ! "hola"

  Thread.sleep(1000)

  system.shutdown()

}