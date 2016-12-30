package river.wang.com.study.execution.future

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.util.control.NonFatal

/**
  * 本例使用Await对象中的ready 和 result 方法。
  * ready 方法会阻塞调用者线程，直到指定的Future对象被完善为止
  * result 方法也会阻塞调用者线程，但是如果Future对象已经被成功完善，
  * 那么该方法就会返回Future对象的完善值；如果完善Future对象的操作执行失败了，
  * 那么该方法会将异常赋予Future对象。
  *
  * Created by wxx on 2016/12/28.
  */
object ComputeBlockingTaskByAwait extends App with ExecutionImplicits {

  val urlSpecSizeFuture = Future {
    val specUrl = "http://www.w3.org/Addressing/URL/url-spec.txt"
    Source.fromURL(specUrl).size
  }
  try {
    val urlSpecSize = Await.result(urlSpecSizeFuture, 1 second)
    // Await.ready(urlSpecSizeFuture, 5 second)
    println(s"url spec contains $urlSpecSize characters")
  } catch {
    case NonFatal(e) => e.printStackTrace()
  }
}
