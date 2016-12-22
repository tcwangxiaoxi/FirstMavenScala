package river.wang.com.study.cookbook.median

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wxx on 2016/12/11.
  */
object MedianSample {

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

    val partitionSize = 5
    val keysPerBuchet = 2

    val rdd = sc.textFile("/wxx/spark/MedianSample/", partitionSize).map(_.toInt)

    val result = MedianUtil.getMedianNum(sc, rdd, keysPerBuchet)

    printf("中位数为：" + result)
    sc.stop()
  }

}
