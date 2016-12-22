package river.wang.com.study.cookbook.websocket.sender

import java.util

import river.wang.com.study.cookbook.websocket.listener.{MyListenerAllRecordersFinishGenerated, MyListenerBus, MyListenerRecorderStartdGenerated}

/**
  * Created by wxx on 2016/12/19.
  */
abstract class AbsMockSender(val createRecordSize: Int, val listenerBus: MyListenerBus) extends MockSender {

  override def run(): Unit = {
    try {
      // 记录一批次测试的数据信息
      val recorders = new util.ArrayList[String]()

      //发送并记录生成的随机数据，用于后面校验
      for (i <- 0 until createRecordSize) {

        //生成数据
        val msg = generator.generateData()

        // 输出：向日志中输出，向Kafka输出
        //      KafkaMsgSender.sendMsg(msg)
        recorders.add(msg)
        println(s"""===生成日志：$msg===""")
        itemCallback(msg)
        listenerBus.postToAll(MyListenerRecorderStartdGenerated())
      }
      finishCallback(recorders)

      listenerBus.postToAll(MyListenerAllRecordersFinishGenerated())
    } catch {
      case e: Exception => {
        e.printStackTrace()
        throw new RuntimeException(e)
      }
    }
  }

  def itemCallback(items: String): Unit = {
  }

  def finishCallback(items: util.ArrayList[String]): Unit = {
  }

}
