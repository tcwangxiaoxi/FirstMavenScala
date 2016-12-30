package river.wang.com.study.execution.future.promise

import java.io.File
import java.util.concurrent.LinkedBlockingDeque

import org.apache.commons.io.monitor._
import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.Promise

/**
  * 本例通过使用Promis对象，
  * 将基于回调函数的API与Future对象桥接起来。
  *
  * Created by wxx on 2016/12/28.
  */
object FileObserverTest extends App with ExecutionImplicits {

  private val queue = new LinkedBlockingDeque[Promise[String]]

  def fileCreated(directory: String): Unit = {
    val fileMonitor = new FileAlterationMonitor(1000)
    val observer = new FileAlterationObserver(directory)
    val listener = new FileAlterationListenerAdaptor {
      override def onFileCreate(file: File): Unit = {
        println(s"onFileCreated: ${file.getName}")
        val p = Promise[String]
        //try p.trySuccess(file.getName) //finally fileMonitor.stop()
        p.success(file.getName)
        queue.put(p)
      }
    }

    observer.addListener(listener)
    fileMonitor.addObserver(observer)
    fileMonitor.start()
  }

  // 创建并启动监听
  fileCreated("./test-datas")

  while (true) {
    val promise = queue.take()
    promise.future.foreach {
      filename => println(s"Detected new file '$filename'")
    }
  }


}
