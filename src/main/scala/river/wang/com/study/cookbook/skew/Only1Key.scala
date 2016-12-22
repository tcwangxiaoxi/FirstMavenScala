package river.wang.com.study.cookbook.skew

import org.apache.spark.{SparkConf, SparkContext}
import river.wang.com.study.cookbook.reverseindex.ReverseIndexUtil

/**
  * Created by wxx on 2016/12/13.
  */
object Only1Key {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf
    conf.setAppName("MedianSample")
    //    conf.set("spark.scheduler.allocation.file",
    //      "C:\\WorkSpaceIdea\\FirstMavenScala\\src\\main\\resources\\fairscheduler.xml")
    conf.set("spark.scheduler.mode", "FAIR")
    conf.set("spark.scheduler.allocation.file",
      "/usr/server/spark/spark-1.6.2-bin-2.5.0-cdh5.2.6/conf/fairscheduler.xml")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //    conf.set("spark.kryo.registrator", "river.wang.com.study.exercise.MyRegistrator")

    val sc = new SparkContext(conf)
    val data = sc.textFile(args(1), args(2).toInt)
    //    data.sample()
    //
    //    result.saveAsTextFile(args(3))

    sc.stop()
  }
}
