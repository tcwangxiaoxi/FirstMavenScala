package org.apache.spark.test

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.{Decoder, StringDecoder}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.{KafkaCluster, KafkaUtils}

import scala.reflect.ClassTag

/**
  * Created by wxx on 2016/11/27.
  */
object KafkaClusterTest {

  def main(args: Array[String]): Unit = {
    val flumeStream = createDirectStream[String, String, StringDecoder, StringDecoder](
      Map("metadata.broker.list" -> "hadoop2:9092,hadoop3:9092,hadoop4:9092", "group.id" -> "testGroup", "auto.offset.reset" -> "smallest"),
      Set("HelloKafkaFromSparkStreaming", "test"))

    println(flumeStream.mkString(","))
  }

  def createDirectStream[
  K: ClassTag,
  V: ClassTag,
  KD <: Decoder[K] : ClassTag,
  VD <: Decoder[V] : ClassTag](
                                kafkaParams: Map[String, String],
                                topics: Set[String]
                              ): Map[TopicAndPartition, Long] = {
    val kc = new KafkaCluster(kafkaParams)
    val fromOffsets = getFromOffsets(kc, kafkaParams, topics)
    fromOffsets
  }

  def getFromOffsets(
                      kc: KafkaCluster,
                      kafkaParams: Map[String, String],
                      topics: Set[String]
                    ): Map[TopicAndPartition, Long] = {
    val reset = kafkaParams.get("auto.offset.reset").map(_.toLowerCase)
    val result = for {
      topicPartitions <- kc.getPartitions(topics).right
      leaderOffsets <- (if (reset == Some("smallest")) {
        kc.getEarliestLeaderOffsets(topicPartitions)
      } else {
        kc.getLatestLeaderOffsets(topicPartitions)
      }).right
    } yield {
      leaderOffsets.map { case (tp, lo) =>
        (tp, lo.offset)
      }
    }
    KafkaCluster.checkErrors(result)
  }
}
