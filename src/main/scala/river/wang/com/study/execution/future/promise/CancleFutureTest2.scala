package river.wang.com.study.execution.future.promise

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.{Future, Promise}

/**
  * Created by wxx on 2016/12/28.
  */
object CancleFutureTest2 extends App with ExecutionImplicits {

  type Cancellable[T] = (Promise[Unit], Future[T])

  class CancellatoinException extends RuntimeException

  /**
    * cancellable 方法会接收异步计算中的代码块b。代码块b会接收单个参数，
    * 检查是否有取消的请求。
    *
    * @param b 异步计算的代码块
    * @tparam T 计算结果类型
    * @return
    */
  def cancellable[T](b: => Future[Unit] => T): Cancellable[T] = {
    // 标识取消请求的Promise对象
    val cancel = Promise[Unit]
    val f = Future {
      val r = b(cancel.future)
      // 如果在Promise对象cancel中调用的tryFailure方法返回了false，
      // 那么表名客户端一定已经完善了Promise对象cancel。
      if (!cancel.tryFailure(new Exception))
        throw new CancellatoinException
      r
    }
    (cancel, f)
  }

  val (cancel, reuslt) = cancellable {
    cancel =>
      var i = 0
      while (i < 5) {
        // 可以使用Promise对象执行取消操作，还可以使用它实现客户端与
        // 执行异步计算的线程之间的双向通讯
        if (cancel.isCompleted) throw new CancellatoinException
        // 模拟执行时间
        Thread.sleep(500)
        println(s"working on $i")
        i += 1
      }
      "result value"
  }

  Thread.sleep(1500)

  /**
    * 注意：调用Promise对象cancel中的trySuccess方法，
    * 无法确保真正取消Future计算。很有可能在客户端有计算取消Future计算前，
    * Promise对象cancel的完善操作就已经执行失败了。因此，
    * 客户端（即接收Future对象完善值的一方，本例中为main线程），
    * 通常应使用trySuccess方法的返回值，检查取消操作是否已经成功执行。
    */
  if (cancel.trySuccess())
    println("computation cancelled!")
  else {
    println("computation cancelling failed!")
  }
  Thread.sleep(10000)

}

