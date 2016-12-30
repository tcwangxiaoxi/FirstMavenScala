package river.wang.com.study.akka.remote

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by wxx on 2016/12/30.
  */
object RemotingSystem {

  def remotingConfig(port: Int) = ConfigFactory.parseString(
    s"""
    akka {
      actor.provider = "akka.remote.RemoteActorRefProvider"
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp{
          hostname = "127.0.0.1"
          port = $port
        }
      }
    }
    """)

  def remotingSystem(name: String, port: Int): ActorSystem = ActorSystem(name, remotingConfig(port))

}
