package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/10.
  */
object StreamingByHDFS extends MyUtils {

  def main(args: Array[String]): Unit = {
    //    val ssc = sparkStreamingContext("StreamingByHDFS", Durations.seconds(10))
    val ssc = sparkServerStreamingContext("StreamingByHDFS", Durations.seconds(10))
    val textStream = ssc.textFileStream("hdfs://hadoopHa/wxx/")
    textStream.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).print()

    ssc.start()
    ssc.awaitTermination()

  }
}