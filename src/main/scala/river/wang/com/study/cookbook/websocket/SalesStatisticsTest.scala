package river.wang.com.study.cookbook.websocket

import java.util

import org.apache.commons.lang.math.NumberUtils
import river.wang.com.study.cookbook.websocket.listener.{LiveListenerBus, MyListener, MyListenerBus}
import river.wang.com.study.cookbook.websocket.queue.BoundsSummaryQueue
import river.wang.com.study.cookbook.websocket.sender.{AbsMockSender, MsgGenerator}
import river.wang.com.study.cookbook.websocket.validator.ValidateService

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Created by wxx on 2016/12/14.
  */
object SalesStatisticsTest {

  def main(args: Array[String]): Unit = {

    val listenerBus = new LiveListenerBus()
    listenerBus.addListener(new MyListener {
      override def onRecorderStartdGenerated(): Unit = {
        println("onRecorderStartdGenerated")
      }

      override def onAllRecordersFinishGenerated(): Unit = {
        println("onAllRecordersFinishGenerated")
      }
    })
    val service = new ValidateService(listenerBus)
    service.start()


  }


  // 本地计算逻辑

  // 统计：校验是否有问题
}

class SalesStatisticsMsgGenerator(val nameItems: Array[String]) extends MsgGenerator {

  override def generateData(): String = {
    val randomIdIndex = Random.nextInt(nameItems.length)
    val randomAmount = Random.nextInt(1000)
    println(s"name,ammount=(${nameItems(randomIdIndex)},$randomAmount)")
    nameItems(randomIdIndex) + "," + randomAmount

  }
}

class SalesStatisticsMockSender(createRecordSize: Int, nameItems: Array[String], windowBatchIntervalTimes: Int, listenerBus: MyListenerBus)
  extends AbsMockSender(createRecordSize, listenerBus) {

  private var lastInfos = new util.HashMap[String, Double]
  private var currentMergeInfos = new util.HashMap[String, Double]
  private val tempMergedInfos = new util.HashMap[String, (Double, Int)]
  private val historyWindowInfos = new util.HashMap[String, BoundsSummaryQueue[Double, Int]]
  private val mergedInfos = new util.HashMap[String, (Double, Int)]

  /**
    * 汇总的操作函数
    */
  override def itemCallback(msg: String): Unit = {
    val msgs = msg.split(",")
    if (msgs.length != 2) {
      throw new RuntimeException("数据错误")
    }

    val key = msgs(0)
    val currentVal = NumberUtils.toDouble(msgs(1))
    val orginVal = currentMergeInfos.asScala.getOrElse(key, 0.0)
    currentMergeInfos.put(key, orginVal + currentVal)
  }

  /**
    * 计算的操作函数
    */
  override def finishCallback(msg: util.ArrayList[String]): Unit = {
    val tempFlagMap = new util.HashMap[String, Double](currentMergeInfos)
    // 根据上一次的值，计算增长趋势
    tempMergedInfos.asScala.foreach {
      case (k, v) =>
        val lastVal = lastInfos.getOrDefault(k, 0.0)
        val curVal = tempFlagMap.getOrDefault(k, 0.0)
        val changeVal = curVal - lastVal
        // 记录本批次的值
        cacheInfo(k, (curVal + v._1, if (changeVal > 0) 1 else if (changeVal < 0) -1 else 0))
        // 去掉已经处理过的数据
        tempFlagMap.remove(k)
    }
    // 遍历本次新增的信息
    if (tempFlagMap != null) {
      tempFlagMap.asScala.map((entry) => {
        // 记录本批次的值
        cacheInfo(entry._1, (entry._2, 1))
        println(s"====tempMergedInfos.put(${entry._1}, (${entry._2}, 1))====")
        entry
      })
    }

    //清理内存
    lastInfos.clear()
    // 保存，用于下次做增长趋势计算
    lastInfos = currentMergeInfos
    currentMergeInfos = new util.HashMap[String, Double]
    // 记录汇总信息
    println(tempMergedInfos.asScala.mkString(","))
    if (needCollectWindowInfo()) {
      // 根据窗口统计数据
      reduceByKeyAndWindow()
    }
  }

  def cacheInfo(key: String, summary: (Double, Int)): Unit = {
    //保存汇总信息
    tempMergedInfos.put(key, (summary._1, summary._2))
    //放入缓存中，用于窗口统计使用
    val sumInfo = historyWindowInfos.getOrDefault(key, new BoundsSummaryQueue[Double, Int](windowBatchIntervalTimes))
    sumInfo.put(summary._1, summary._2)
    historyWindowInfos.put(key, sumInfo)
  }

  def needCollectWindowInfo(): Boolean = {
    // TODO：需要根据滑动时间决定
    true
  }

  def reduceByKeyAndWindow(): Unit = {
    historyWindowInfos.asScala.foreach {
      case (k, v) =>
        val info = v.getSummaryInfo
        println(s"""=====array:${info._2.mkString(",")}""")
        println(s"""key：$k；value：${info._1},${info._2.sum}""")
    }
  }

  override val generator = new SalesStatisticsMsgGenerator(nameItems)

}
