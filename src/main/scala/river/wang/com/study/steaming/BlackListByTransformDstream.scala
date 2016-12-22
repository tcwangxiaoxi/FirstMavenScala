package river.wang.com.study.steaming

import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/12.
  */
object BlackListByTransformDstream extends MyUtils {

  def main(args: Array[String]): Unit = {
    val (appName, duration) = ("BlackListByTransformDstream", Durations.seconds(15))
    val ssc = sparkStreamingContext(appName, duration)
    //    val ssc = sparkServerStreamingContext(appName,duration)

    val blackList = Array(("hadoop", true), ("mahout", true))
    val blackListRdd = ssc.sparkContext.parallelize(blackList)

    val textStream = ssc.socketTextStream("hadoop1", 9999)

    val mapStream = textStream.map(ads => (ads.split(" ")(1), ads))
    mapStream.transform(itemRdd => {
      val joinedRdd = itemRdd.leftOuterJoin(blackListRdd)
      joinedRdd.filter(!_._2._2.getOrElse(false)).map(_._2._1)
    }).print()

    ssc.start()
    ssc.awaitTermination()

  }
}