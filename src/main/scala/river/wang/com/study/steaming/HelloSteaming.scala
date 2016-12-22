package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/10.
  */
object HelloSteaming extends MyUtils {
  def main(args: Array[String]): Unit = {
    val ssc = sparkStreamingContext("HelloSteaming", Durations.seconds(15))
    val textStream = ssc.socketTextStream("hadoop1", 9999)
    val countMap = textStream.flatMap(_.split(" ")).map((_, 1)).cache()
    countMap.reduceByKey(_ + _).print()
//    countMap.filter(_._1.equals("hadoop")).print()


    ssc.start()
    ssc.awaitTermination()

  }
}
