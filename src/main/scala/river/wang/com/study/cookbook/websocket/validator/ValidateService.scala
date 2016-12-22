package river.wang.com.study.cookbook.websocket.validator

import java.util.concurrent.TimeUnit._
import java.util.concurrent.{Executors, ThreadFactory}

import river.wang.com.study.common.Interruptable
import river.wang.com.study.cookbook.websocket.SalesStatisticsMockSender
import river.wang.com.study.cookbook.websocket.listener.LiveListenerBus

/**
  * Created by wxx on 2016/12/20.
  */
class ValidateService(listenerBus: LiveListenerBus) extends Interruptable {

  val nameItems = Array("a", "b", "c", "d", "e")

  val createRecordSize = 5
  val batchDuration = 3
  val windowDuration = 6

  val windowBatchIntervalTimes = windowDuration / batchDuration

  val executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory {
    override def newThread(r: Runnable): Thread = new Thread(r, "==ValidateService")
  })

  def start(): Unit = {
    listenerBus.start(this)
    executorService.scheduleAtFixedRate(
      new SalesStatisticsMockSender(createRecordSize, nameItems, windowBatchIntervalTimes, listenerBus),
      1, batchDuration, SECONDS)
  }

  override def stop(): Unit = {
    /*// 没用
    if (AsynchronousListenerBus.withinListenerThread.value) {
      throw new SparkException("Cannot stop SparkContext within listener thread of" +
        " AsynchronousListenerBus")
    }*/
    executorService.shutdown()
  }

}
