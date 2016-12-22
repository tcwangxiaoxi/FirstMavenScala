package org.apache.spark.test

import java.util.concurrent.{ScheduledExecutorService, TimeUnit}

import org.apache.curator.utils.ThreadUtils
import org.apache.spark.{Logging, SparkContext}

/**
  * Created by wxx on 2016/12/6.
  */
class TimeoutJobTrack(val sc: SparkContext) extends Runnable with Logging {

  private var timer: ScheduledExecutorService = _

  def start(): Unit = {
    // 定时检查，并对超时数据进行停止Job
    timer = ThreadUtils.newSingleThreadScheduledExecutor("timer-thread")
    timer.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS)
  }

  override def run(): Unit = {
    sc.jobProgressListener.activeJobs.filter(job =>
      System.currentTimeMillis() - job._2.submissionTime.get > 5000
    ).foreach(job => {
      sc.cancelJob(jobId = job._1)
      logInfo(s"================================正在停止任务运行(${job})==================================")
    })
  }

}

object TimeoutJobTrack {
  def apply(sc: SparkContext): TimeoutJobTrack = new TimeoutJobTrack(sc)
}
