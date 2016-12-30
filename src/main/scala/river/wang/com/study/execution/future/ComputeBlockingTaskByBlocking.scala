package river.wang.com.study.execution.future

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  * 在异步计算内部执行阻塞操作时，
  * 可以通过使用 blocking 封装调用语句，
  * 当global执行上下文检测到任务的数量比处理任务线程的数量多时，
  * global执行上下文就会生成额外的线程。
  *
  * Created by wxx on 2016/12/28.
  */
object ComputeBlockingTaskByBlocking extends App with ExecutionImplicits {

  private val start = System.nanoTime

  val futures = for (_ <- 0 until 16) yield Future {
    import scala.concurrent.blocking
    blocking {
      Thread.sleep(1000)
    }
  }

  futures.map(Await.ready(_, Duration.Inf))

  val end = System.nanoTime

  println(s"Total time = ${(end - start) / (1000 * 1000 * 1000)}s")
  println(s"Total CPUs = ${Runtime.getRuntime.availableProcessors()}")

}
