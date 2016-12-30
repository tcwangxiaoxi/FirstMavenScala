package river.wang.com.study.akka.router

import akka.actor._
import river.wang.com.study.akka.MyActorSystem
import river.wang.com.study.akka.lifecycle.StringPrinter

/**
  * 转发模式
  *
  * Created by wxx on 2016/12/30.
  */
object Router extends App {

  private val router = MyActorSystem.ourActorSystem.actorOf(Props[Router], "router")

  router ! "Hola"
  router ! "Hey"
}

class Router extends Actor {

  var i = 0
  val children = for (_ <- 0 until 4) yield
    context.actorOf(Props[StringPrinter])

  override def receive: Receive = {
    case msg =>

      /**
        * 某些Actor对象的作用仅是为其他Actor对象转发消息。
        * 例如：有些Actor对象专门负责多个Actor对象负载均衡，
        * 有些Actor对象会将消息转发给他们的镜像对象，以确保更好的可用性。
        * 在这些情况中，在转发消息时不更改消息中的sender字段具有实际意义。
        * Actor对象引用中的forward方法可以实现该目标。
        */
      children(i) forward msg
      i = (i + 1) % 4
  }
}
