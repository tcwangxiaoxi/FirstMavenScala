package river.wang.com.study.cookbook.websocket

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.io.Source
import scala.reflect.io.Path


/**
  * Kafka Producer
  */
class KafkaMsgSender private(brokerList: String, topic: String) extends Runnable {

  private val BROKER_LIST = brokerList
  //"master:9092,worker1:9092,worker2:9092"
  private val TARGET_TOPIC = topic
  //"new"
  private val DIR = "/root/Documents/"

  /**
    * 1、配置属性
    * metadata.broker.list : kafka集群的broker，只需指定2个即可
    * serializer.class : 如何序列化发送消息
    * request.required.acks : 1代表需要broker接收到消息后acknowledgment,默认是0
    * producer.type : 默认就是同步sync
    */
  private val props = new Properties()
  props.put("bootstrap.servers", this.BROKER_LIST)
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("request.required.acks", "1")
  props.put("producer.type", "async")


  /**
    * 2、创建Producer
    */
  private val producer = new KafkaProducer[String, String](props)

  /**
    * 3、产生并发送消息
    * 搜索目录dir下的所有包含“transaction”的文件并将每行的记录以消息的形式发送到kafka
    *
    */
  def run(): Unit = {
    while (true) {
      val files = Path(this.DIR).walkFilter(p => p.isFile && p.name.contains("transaction"))

      try {
        for (file <- files) {
          val reader = Source.fromFile(file.toString(), "UTF-8")

          for (line <- reader.getLines()) {
            val message = new ProducerRecord[String, String](this.TARGET_TOPIC, line)
            producer.send(message)
          }

          //produce完成后，将文件copy到另一个目录，之后delete
          val fileName = file.toFile.name
          file.toFile.copyTo(Path("/root/Documents/completed/" + fileName + ".completed"))
          file.delete()
        }
      } catch {
        case e: Exception => println(e)
      }

      try {
        //sleep for 3 seconds after send a micro batch of message
        Thread.sleep(3000)
      } catch {
        case e: Exception => println(e)
      }
    }
  }

  private def sendMsg(msg: String): Unit = {
    val message = new ProducerRecord[String, String](this.TARGET_TOPIC, msg)
    producer.send(message)
  }
}

object KafkaMsgSender {

  lazy val sender = new KafkaMsgSender("hadoop2:9092,hadoop3:9092,hadoop4:9092", "SalesStatistics")

  def sendMsg(msg: String): Unit = {
    sender.sendMsg(msg)
  }

  def main(args: Array[String]): Unit = {
    // test
    KafkaMsgSender.sendMsg("wangxiaoxi")
  }

}
