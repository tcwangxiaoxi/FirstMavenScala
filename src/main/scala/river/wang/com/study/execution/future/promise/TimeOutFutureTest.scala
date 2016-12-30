package river.wang.com.study.execution.future.promise

import java.util._

import river.wang.com.study.execution.utils.ExecutionImplicits

import scala.concurrent.{Future, Promise}

class TimeOutFutureTest {

  private val timer = new Timer(true)

  def timeout(t: Long): Future[Unit] = {
    val p = Promise[Unit]
    timer.schedule(new TimerTask {
      override def run(): Unit = {
        p.success()
        timer.cancel()
      }
    }, t)
    p.future
  }
}

/**
  * Created by wxx on 2016/12/28.
  */
object TimeOutFutureTest extends App with ExecutionImplicits {

  private val f = new TimeOutFutureTest()
  f.timeout(1000).foreach(_ => println("Timed out !"))

  Thread.sleep(2000)
}
