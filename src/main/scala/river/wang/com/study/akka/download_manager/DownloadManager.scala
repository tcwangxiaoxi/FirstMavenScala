package river.wang.com.study.akka.download_manager

import java.io.FileNotFoundException

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor._
import akka.event.Logging
import river.wang.com.study.akka.MyActorSystem

import scala.collection.mutable
import scala.concurrent.duration._


/**
  * Created by wxx on 2016/12/30.
  */
object DownloadManager extends App {

  case class Download(url: String, dest: String)

  case class Finished(dest: String)

  private val downloadManager = MyActorSystem.ourActorSystem.actorOf(Props(classOf[DownloadManager], 4), "main")
  downloadManager ! Download("http://www.w3.org/Addressing/URL/url-spec.txt", "test-datas/url-spec.txt")

  downloadManager ! Download("http://www.w3.org/Addressing/URL/url-spec1.txt", "test-datas/url-spec1.txt")


}

class DownloadManager(val downloadSlots: Int) extends Actor {

  import DownloadManager._

  val log = Logging(context.system, this)

  /**
    * 用于存储可用Downloader对象的引用。
    */
  val downloaders = mutable.Queue[ActorRef]()
  /**
    * 用于存储需要缓存的Download消息
    */
  private val pendingWork = mutable.Queue[Download]()

  /**
    * 用于 Downloader实例引用 - Download 消息对
    */
  private val workItems = mutable.Map[ActorRef, Download]()

  private def checkDownloads(): Unit = {
    if (pendingWork.nonEmpty && downloaders.nonEmpty) {
      val dl = downloaders.dequeue()
      val item = pendingWork.dequeue()
      log.info(s"$item starts, ${downloaders.size} download slots left")

      dl ! item
      workItems(dl) = item
    }
  }

  override def receive: Receive = {
    case msg@Download(url, dest) =>
      // 把任务压入待处理队列
      pendingWork.enqueue(msg)
      // 检查是否有可处理任务,如果可以处理，则发送给worker进行下载
      checkDownloads()
    case Finished(dest) =>
      workItems.remove(sender)
      downloaders.enqueue(sender)
      log.info(s"$dest done, ${downloaders.size} download slots left")
      checkDownloads()
  }

  /**
    * 为了确保 DownloadManager 对象拥有指定数量的子Actor对象Downloader，
    * 我们重写了preStart方法，以便创建 Downloader 对象，并将这些对象的引用
    * 添加到Downloaders队列中
    *
    */
  override def preStart(): Unit = {
    for (i <- 0 until downloadSlots) {
      val dl = context.actorOf(Props[Downloader], s"dl$i")
      downloaders.enqueue(dl)
    }
  }

  /**
    * 考虑到 URL 非法的情况，Actor对象会由于 FileNotFoundException 异常而失效。
    * 因此，需要从 workItems 映射移除这类 Actor 对象，并将这些对象的引用重新添加到
    * downloaders 队列中。在这种情况中重启Downloader 对象是没有意义的，因为它们不会包含任何状态。
    * 我们只需要恢复无法处理 URL 的 Downloader 对象。如果 Downloader 实例因其他类型的异常失效了，
    * 应将该异常变为 DownloadManager 对象的异常并使该对象失效。
    */
  override val supervisorStrategy = OneForOneStrategy(20, 2 second) {
    case fnf: FileNotFoundException =>
      log.info(s"Resource could not be found: $fnf")
      workItems.remove(sender)
      downloaders.enqueue(sender)
      Resume
    case _ =>
      Escalate
  }
}
