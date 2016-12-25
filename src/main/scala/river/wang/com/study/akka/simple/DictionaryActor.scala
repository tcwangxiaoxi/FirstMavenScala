package river.wang.com.study.akka.simple

import java.util

import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

/**
  * Created by wxx on 2016/12/23.
  */
class DictionaryActor extends Actor {

  val log = Logging(context.system, this)

  private val dictSet = new util.HashSet[String]

  override def receive: Receive = init

  def init: Receive = {
    case Init(path) =>
      dictSet.add(path)
      log.info("已经初始化")
      context.become(search)
    case IsWord | End | _ =>
      log.info("未经初始化")
  }

  def search: Receive = {
    case Init(_) =>
      log.info("已经完成初始化")
    case IsWord(word) =>
      if (dictSet.contains(word)) {
        log.info(s"已经查询到$word")
      } else {
        log.info(s"没有该单词$word")
      }
    case End =>
      log.info("停止服务")
      dictSet.clear()
      context.become(init)
  }

  override def unhandled(message: Any): Unit = {
    log.info(s"$message 无效")
    super.unhandled(message)
  }

}

case class Init(path: String)

case class IsWord(w: String)

case object End

object DictionaryActor extends App {

  private val system = MyActorSystem.ourActorSystem

  val dictActor: ActorRef = system.actorOf(Props[DictionaryActor], name = "dict")


  dictActor ! "hi"
  Thread.sleep(1000)

  dictActor ! Init("hi")
  Thread.sleep(1000)

  dictActor ! "aaa"
  Thread.sleep(1000)

  dictActor ! IsWord("aaa")
  Thread.sleep(1000)

  dictActor ! IsWord("hi")
  Thread.sleep(1000)

  dictActor ! End
  Thread.sleep(1000)

  dictActor ! "aaa"
  Thread.sleep(1000)

  dictActor ! Init("aaa")
  Thread.sleep(1000)

  dictActor ! IsWord("aaa")
  Thread.sleep(1000)

  system.shutdown()

}
