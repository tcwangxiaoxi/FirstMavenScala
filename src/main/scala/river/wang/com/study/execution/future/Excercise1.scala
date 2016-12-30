package river.wang.com.study.execution.future

import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Timer, TimerTask}

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.Future

/**
  * Created by wxx on 2016/12/28.
  */
object Excercise1 extends App with ExecutionImplicits {

  // 运行时间
  val timeout = 20000
  // 超时时间
  val needtime = 10000

  type Cancelable[T] = (Future[T], AtomicBoolean)

  // 这个使用AtomicBoolean的方式没有使用Permise好，
  // 暴露出去的变量容易出现问题
  def cancellable[T](f: => AtomicBoolean => T): Cancelable[T] = {
    val isStoped = new AtomicBoolean(false)
    val fu = Future {
      val r = f(isStoped)
      if (isStoped.get)
        throw new RuntimeException
      r
    }
    (fu, isStoped)
  }

  val (task, isStoped) = cancellable {
    cancel =>
      var i = 0
      val stepTimes = 10
      while (i <= stepTimes) {
        if (cancel.get) throw new RuntimeException
        Thread.sleep(needtime / stepTimes)
        println(s"finished ${i * 100 / stepTimes}/100")
        i += 1
      }
      "finish"
  }
  task.foreach(println)
  task.onFailure {
    case e => println("执行超时")
  }

  new Timer().schedule(new TimerTask {
    override def run(): Unit = {
      isStoped.compareAndSet(false, true)
    }
  }, timeout)

  while (!task.isCompleted) {
    print(".")
    Thread.sleep(500)
  }

}
