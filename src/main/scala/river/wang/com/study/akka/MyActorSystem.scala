package river.wang.com.study.akka

import akka.actor.ActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
object MyActorSystem {

  lazy val ourActorSystem = ActorSystem("OurExampleSystem")

}
