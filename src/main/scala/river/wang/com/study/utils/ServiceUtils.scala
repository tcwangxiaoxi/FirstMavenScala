package river.wang.com.study.utils

import org.apache.spark.Logging
import river.wang.com.study.common.Interruptable

import scala.util.control.{ControlThrowable, NonFatal}

/**
  * Created by wxx on 2016/12/20.
  */
object ServiceUtils extends Logging {

  /**
    * 执行一段代码，如果该代码中出现了异常，则停止服务的运行
    *
    */
  def tryOrStopSparkContext(service: Interruptable)(block: => Unit) {
    try {
      block
    } catch {
      case e: ControlThrowable => throw e
      case t: Throwable =>
        val currentThreadName = Thread.currentThread().getName
        if (service != null) {
          logError(s"uncaught error in thread $currentThreadName, stopping SparkContext", t)
          service.stop()
        }
        if (!NonFatal(t)) {
          logError(s"throw uncaught fatal error in thread $currentThreadName", t)
          throw t
        }
    }
  }
}
