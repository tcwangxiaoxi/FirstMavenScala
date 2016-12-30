package river.wang.com.study.execution.future.promise

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal

/**
  * Created by wxx on 2016/12/28.
  */
object MyFutureTest extends App with ExecutionImplicits {

  /**
    * 通过Promise对象，实现自定义的Future操作
    *
    * @param f 实际操作的方法定义
    * @tparam T 返回值类型
    * @return
    */
  def myFuture[T](f: => T)(implicit executor: ExecutionContext): Future[T] = {
    val p = Promise[T]
    executor.execute(new Runnable {
      override def run(): Unit = {
        try {
          p.success(f)
        } catch {
          case NonFatal(t) => p.failure(t)
        }
      }
    })
    p.future
  }

  /**
    * 实际应用自定义的Future
    */
  myFuture {
    Thread.sleep(1000)
    "asdddd"
  }.foreach {
    text =>
      println(text)
  }

  Thread.sleep(3000)
}
