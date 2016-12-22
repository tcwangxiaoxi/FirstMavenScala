package river.wang.com.study.steaming

import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Durations, StreamingContext}
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/27.
  */
object RecoverableContext extends MyUtils {

  def createContext(kafkaUrl: String, gourpId: String, topic: Set[String], appName: String, checkPointUrl: String) = {
    val ssc = sparkStreamingContext(appName, Durations.seconds(15))

    ssc.checkpoint(checkPointUrl)

    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
      Map("metadata.broker.list" -> kafkaUrl, "group.id" -> gourpId), topic)

    kafkaStream.flatMap(_._2.split(" ")).map((_, 1)).reduceByKey(_ + _).map(_._1).print()
    ssc
  }

  def main(args: Array[String]) {


    val checkPointDir = "hdfs://hadoopHa/wxx/spark/checkpoint/"
    val appName = "RecoverableContext1"
    val (kafkaUrl, gourpId, topic) = ("hadoop2:9092,hadoop3:9092,hadoop4:9092", "testGroup", Set("test"))
    val checkPointUrl = checkPointDir + appName

    val ssc = StreamingContext.getOrCreate(checkPointUrl,
      () => {
        createContext(kafkaUrl, gourpId, topic, appName, checkPointUrl)
      })
    ssc.start()
    ssc.awaitTermination()
  }
}
