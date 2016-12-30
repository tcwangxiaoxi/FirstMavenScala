package river.wang.com.study.akka.stop

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem
import river.wang.com.study.akka.pingpong.Pongy
import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.util.{Failure, Success}

/**
  * Created by wxx on 2016/12/30.
  */
class GracefulPingy extends Actor {
  val system = MyActorSystem.ourActorSystem
  val log = Logging(system, GracefulPingy.getClass)

  private val pongy = context.actorOf(Props[Pongy], "pongy")

  context.watch(pongy)

  override def receive: Receive = {
    case "Die,Pingy!" =>
      log.info("context.stop(pongy)")
      //      Thread.sleep(5000)
      context.stop(pongy)
    case Terminated(`pongy`) =>
      log.info("context.stop(self)")
      context.stop(self)
    case "work" =>
      log.info("working!!")
      Thread.sleep(5000)
  }
}

object GracefulPingy extends App with ExecutionImplicits {

  val system = MyActorSystem.ourActorSystem
  val grace = system.actorOf(Props[GracefulPingy], "grace")

  val log = Logging(system, GracefulPingy.getClass)

  import akka.pattern._

  import scala.concurrent.duration._

  val stopped = gracefulStop(grace, 3 second, "Die,Pingy!")

  stopped onComplete {
    case Success(x) => log.info("graceful shutdown successful")
      system.shutdown()
    case Failure(t) => log.info("grace not stopped!")
      system.shutdown()
  }


}
