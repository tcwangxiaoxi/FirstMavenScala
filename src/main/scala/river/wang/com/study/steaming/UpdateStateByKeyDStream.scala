package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/12.
  */
object UpdateStateByKeyDStream extends MyUtils {

  def main(args: Array[String]): Unit = {

    val (appName, duration) = ("UpdateStateByKeyDStream", Durations.seconds(15))
    val ssc = sparkStreamingContext(appName, duration)
    //    val ssc = sparkServerStreamingContext(appName,duration)

    ssc.checkpoint(s"hdfs://hadoopHa/wxx/spark/checkpoint/${appName}")

    val textStream = ssc.socketTextStream("hadoop1", 9999)

    val mapStream = textStream.flatMap(_.split(" ")).map((_, 1))


    /*mapStream.updateStateByKey[Int]((seq: Seq[Int], old: Option[Int]) => {
      Some(seq.sum + old.getOrElse(0))
    }).print()*/

    mapStream.updateStateByKey[(Int, Int)]((seq: Seq[(Int)], old: Option[(Int, Int)]) => {
      Some(seq.sum + old.getOrElse((0, 0))._1, 0)
    }).print()

    ssc.start()
    ssc.awaitTermination()

  }
}