package river.wang.com.study.steaming

import kafka.serializer.StringDecoder
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.kafka.KafkaUtils
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/10.
  */
object StreamingByKafka extends MyUtils {

  def main(args: Array[String]): Unit = {
    val ssc = sparkStreamingContext("StreamingByKafka", Durations.seconds(15))
    //    val ssc = sparkServerStreamingContext("StreamingByKafka", Durations.seconds(15))

    /**
      * receiver 方式
      */
    //            val kafkaStream = KafkaUtils.createStream(ssc, "hadoop2:2181,hadoop3:2181,hadoop4:2181",
    //              "MyFirstConsumerGroup", Map("HelloKafkaFromSparkStreaming" -> 2))

    /**
      * direct 方式
      */
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
      Map("metadata.broker.list" -> "hadoop2:9092,hadoop3:9092,hadoop4:9092", "group.id" -> "testGroup"),
      Set("HelloKafkaFromSparkStreaming", "test"))

    kafkaStream.flatMap(_._2.split(" ")).map((_, 1)).reduceByKey(_ + _).print()

    ssc.start()
    ssc.awaitTermination()



  }
}
















