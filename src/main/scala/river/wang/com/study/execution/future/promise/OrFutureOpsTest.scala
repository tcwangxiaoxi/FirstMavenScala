package river.wang.com.study.execution.future.promise

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.{Future, Promise}

/**
  *
  * 通过类的隐式转换，实现扩展原有类型方法的目的，
  * 实现自定义的API方法
  * Created by wxx on 2016/12/28.
  */
object OrFutureOpsTest extends App with ExecutionImplicits {

  implicit class FutureOps[T](val self: Future[T]) {
    def or(that: Future[T]): Future[T] = {
      val p = Promise[T]
      self.onComplete { x => p tryComplete x }
      that.onComplete { y => p tryComplete y }
      p.future
    }
  }

  val f1 = Future {
    Thread.sleep(1000)
    "1000"
  }
  val f2 = Future {
    Thread.sleep(500)
    "500"
  }

  f1 or f2 foreach println
}



