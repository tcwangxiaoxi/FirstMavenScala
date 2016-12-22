package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/12.
  */
object HotestKeyByReduceKeyAndWindow extends MyUtils {


  def main(args: Array[String]): Unit = {
    val (appName, duration) = ("HotestKeyByReduceKeyAndWindow1", Durations.seconds(30))
    val ssc = sparkStreamingContext(appName, duration)
    //    val ssc = sparkServerStreamingContext(appName,duration)

    // 测试数据0-5s: 1 1 1 1 1
    // 测试数据5-10s: 2 2 2 2 2
    // 测试数据10-15s: 3 3 3 3 3
    // 测试数据15-20s: 4 4 4 4 4
    // 测试数据20-25s: 5 5 5 5 5
    // 测试数据25-30s: 6 6 6 6 6

    ssc.checkpoint(s"hdfs://hadoopHa/wxx/spark/checkpoint/${appName}")

    val textStream = ssc.socketTextStream("hadoop1", 9999)

    val mapStream = textStream.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).cache()

    mapStream.foreachRDD(rdd => {
      rdd.collect().foreach(println)
    })

    /*val result = mapStream.reduceByKeyAndWindow((x: Int, y: Int) => x + y, Durations.seconds(90), Durations.seconds(30))
    result.foreachRDD(rdd => {
      val d = rdd//.cache()
      d.checkpoint()
      d.collect().foreach(println)
    })*/

    val result = mapStream.reduceByKeyAndWindow(_ + _, _ - _, Durations.seconds(90), Durations.seconds(30)).cache()
    result.foreachRDD(rdd => {
      rdd.collect().foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()

  }
}