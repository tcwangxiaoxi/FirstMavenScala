package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.flume.FlumeUtils
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/10.
  */
object StreamingByFlume extends MyUtils {

  def main(args: Array[String]): Unit = {
    //val ssc = sparkStreamingContext("StreamingByFlume", Durations.seconds(10))
    val ssc = sparkServerStreamingContext("StreamingByFlume", Durations.seconds(15))
    //host不能是driver或master的地址，因为receiver是运行在worker节点上的，所以这个host只能是worker的一个地址
    val flumeStream = FlumeUtils.createStream(ssc, "hadoop3", 9999)
    flumeStream.flatMap(event => new String(event.event.getBody.array()).split(" "))
      .map((_, 1)).reduceByKey(_ + _).print()


    ssc.start()
    ssc.awaitTermination()

  }
}