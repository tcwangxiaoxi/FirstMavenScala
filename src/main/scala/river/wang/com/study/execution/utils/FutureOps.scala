package river.wang.com.study.execution.utils

import scala.concurrent.{Future, Promise}

/**
  * 通过隐式类的功能，实现原有类方法的扩展，
  * 通过定义trait的方式，可以实现通过继承方式进行扩展
  * Created by wxx on 2016/12/28.
  */
trait FutureOps extends ExecutionImplicits {

  implicit class FutureOpsByTrait[T](val self: Future[T]) {
    def or(that: Future[T]): Future[T] = {
      FutureOps.FutureOpsByObj(self).or(that)
    }
  }

}

/**
  * 通过隐式类的功能，实现原有类方法的扩展，
  * 通过定义object的方式，可以实现通过导入（import）方式进行扩展
  * Created by wxx on 2016/12/28.
  */
object FutureOps extends ExecutionImplicits {

  implicit class FutureOpsByObj[T](val self: Future[T]) {
    def or(that: Future[T]): Future[T] = {
      val p = Promise[T]
      self.onComplete { x => p tryComplete x }
      that.onComplete { y => p tryComplete y }
      p.future
    }
  }

}