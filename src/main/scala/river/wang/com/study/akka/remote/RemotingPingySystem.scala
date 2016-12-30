package river.wang.com.study.akka.remote

import akka.actor.Props

import scala.concurrent.duration.Duration

/**
  * Created by wxx on 2016/12/30.
  */
object RemotingPingySystem extends App {

  private val system = RemotingSystem.remotingSystem("PingyDimension", 24567)
  private val runner = system.actorOf(Props[PingPongRunner], "runner")

  runner ! "start"

  system.awaitTermination(Duration.Undefined)
}
