package river.wang.com.study.akka.remote

import akka.actor.Props
import river.wang.com.study.akka.pingpong.Pongy

import scala.concurrent.duration.Duration

/**
  * Created by wxx on 2016/12/30.
  */
object RemotingPongySystem extends App {

  private val system = RemotingSystem.remotingSystem("PongyDimension", 24321)
  private val pongy = system.actorOf(Props[Pongy], "pongy")

  system.awaitTermination(Duration.Undefined)
}
