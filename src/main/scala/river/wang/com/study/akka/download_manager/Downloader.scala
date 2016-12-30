package river.wang.com.study.akka.download_manager

import java.io.File

import akka.actor._
import com.google.common.io.Files

import scala.io.Source

/**
  * Created by wxx on 2016/12/30.
  */
class Downloader extends Actor {
  override def receive: Receive = {
    case DownloadManager.Download(url, dest) =>
      val content = Source.fromURL(url)
      Files.write(content.mkString.getBytes, new File(dest))
      sender ! DownloadManager.Finished(dest)
  }
}
