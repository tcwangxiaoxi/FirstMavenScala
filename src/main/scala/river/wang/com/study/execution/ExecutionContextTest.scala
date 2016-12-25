package river.wang.com.study.execution

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.ExecutionContext
import scala.concurrent.forkjoin.ForkJoinPool

/**
  * Created by wxx on 2016/12/25.
  */
object ExecutionContextTest extends App {

  // 默认的ExecutionContext对象
  private val ectx = ExecutionContext.global
  ectx.execute(new Runnable {
    override def run(): Unit = {
      println("Running on the execution context.")
    }
  })

  private val pool = new ForkJoinPool(2)
  private val ser = ExecutionContext.fromExecutorService(pool)
  ser.execute(new Runnable {
    override def run(): Unit = {
      println("Running on the execution context again.")
    }
  })

  def execute(body: => Unit) = ExecutionContext.global.execute(
    new Runnable {
      override def run(): Unit = body
    }
  )

  for (i <- 0 until 32)
    execute {
      Thread.sleep(2000)
      println(s"Task $i completed.")
    }

  private val long = new AtomicLong(0)
  long.compareAndSet(0,1)

  Thread.sleep(60*1000)
}
