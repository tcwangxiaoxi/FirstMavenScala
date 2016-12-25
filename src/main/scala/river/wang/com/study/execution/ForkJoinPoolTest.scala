package river.wang.com.study.execution

import java.util.concurrent.{ForkJoinPool, TimeUnit}

/**
  * Created by wxx on 2016/12/25.
  */
object ForkJoinPoolTest extends App {

  val run = new Runnable {
    override def run(): Unit = {
      Thread.sleep(400)
      println("该任务是异步运行的")
    }
  }

  // ForkJoinPool线程池默认是守护线程
  private val executor = new ForkJoinPool
  executor.execute(run)

  // Executors.newCachedThreadPool线程池默认是用户线程
  //  private val pool = Executors.newCachedThreadPool()
  //  pool.execute(run)

  // 用户线程和守护线程的区别在于，
  // 用户线程是，在主线程结束前，会等待用户线程结束
  // 守护线程则不会等待
  println(123)
  executor.shutdown()
  executor.awaitTermination(60, TimeUnit.SECONDS)
}
