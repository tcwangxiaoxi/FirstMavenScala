package river.wang.com.study.execution.future


import java.lang.Thread.UncaughtExceptionHandler

import scala.concurrent.forkjoin.{ForkJoinPool, ForkJoinWorkerThread}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/**
  * Created by wxx on 2016/12/25.
  */
object ExceptionFutureTest {

  implicit val ectx = ExecutionContext.fromExecutorService(new ForkJoinPool(
    Runtime.getRuntime.availableProcessors, new DefaultForkJoinWorkerThreadFactory, new UncaughtExceptionHandler {
      override def uncaughtException(t: Thread, e: Throwable): Unit = {
        println("!!!!!!!!!!!!发生致命错误！" + e.getMessage)
      }
    }, false
  ), (t) => {
    println("发生致命错误！" + t.getMessage)
  })

  def main(args: Array[String]): Unit = {

    val urlSpec: Future[List[Int]] = Future {
      Thread.sleep(1000)
      println("输入参数错误")

      /**
        * InterruptedException异常和一些严重的程序错误
        * （如：LinkageError、VirtualMachineError、ThreadDeath和Scala程序中的ControlThrowable错误），
        * 都会被转发给执行上下文的reportFailure方法。
        *
        * Future计算不会捕捉致命错误。使用NonFatal提取器可以找出非致命错误。
        * 所以致命错误需要自己catch并进行处理
        */
      throw new InterruptedException("致命错误")
      //      throw new IllegalArgumentException("输入参数错误")
      //    List(1, 2, 3)
    }

    /**
      * 该方法用于检测异常。
      * 如果Future计算抛出异常，则回调该方法，
      * 如果正常则调用下面没有failed的方法（多个foreach方法是异步执行的）
      */
    urlSpec.failed.foreach {
      // 只能对非致命错误进行处理
      case NonFatal(t) => println(s"有非致命错误 - $t")
      // 无效的捕获，致命错误由reportFailure方法处理
      case e: InterruptedException => println(s"有致命错误 - $e")
    }

    urlSpec.foreach((items: List[Int]) => {
      println("=====1=====" + items.mkString(","))
    })

    // 这个与上面的效果完全一样，只是简便写法
    urlSpec.foreach {
      items =>
        println("=====2=====" + items.mkString(","))
    }

    urlSpec.onComplete {
      case Failure(error) => println(s"有异常 - $error")
      case Success(items) => println("=====3=====" + items.mkString(","))
    }
    Thread.sleep(60 * 1000)
  }
}

class MyForkJoinWorkerThread(pool: ForkJoinPool) extends ForkJoinWorkerThread(pool: ForkJoinPool) {
  override def onTermination(t: Throwable): Unit = {
    if (t != null)
      println("发生致命错误!!!!" + t.getMessage)
  }
}

class DefaultForkJoinWorkerThreadFactory extends ForkJoinPool.ForkJoinWorkerThreadFactory {
  override def newThread(forkJoinPool: ForkJoinPool): ForkJoinWorkerThread = {
    new MyForkJoinWorkerThread(forkJoinPool)
  }
}

