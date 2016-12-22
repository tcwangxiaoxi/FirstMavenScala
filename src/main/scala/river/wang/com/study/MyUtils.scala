package river.wang.com.study

import java.util
import java.util.Comparator

import jodd.util.collection.SortedArrayList
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by wxx on 2016/10/19.
  */
trait MyUtils {

  def sparkContext(name: String, logLevel: String = "INFO", master: Option[String] = Some("local")): SparkContext = {
    val conf = new SparkConf
    conf.setAppName(name)

    master match {
      case Some(info) if info.endsWith("[2]") =>
        conf.set("spark.driver.allowMultipleContexts", "true").setMaster(info)
      case Some(info) => conf.setMaster(info)
    }

    val sc = new SparkContext(conf)
    sc.setLogLevel(logLevel)
    sc
  }

  def sparkServerContext(name: String, logLevel: String = "INFO"): SparkContext = {
    sparkContext(name, logLevel, None)
  }

  def sparkSQLContext(name: String, logLevel: String = "INFO", master: Option[String] = Some("local")): SQLContext = {
    val sc = sparkContext(name, logLevel, master)
    new SQLContext(sc)
  }

  def sparkServerSQLContext(name: String, logLevel: String = "INFO"): SQLContext = {
    sparkSQLContext(name, logLevel, None)
  }

  def sparkHiveContext(name: String, logLevel: String = "INFO", master: Option[String] = Some("local")): HiveContext = {
    val sc = sparkContext(name, logLevel, master)
    new HiveContext(sc)
  }

  def sparkServerHiveContext(name: String, logLevel: String = "INFO"): HiveContext = {
    sparkHiveContext(name, logLevel, None)
  }

  def sparkStreamingContext(name: String, batchDuration: Duration, logLevel: String = "INFO"): StreamingContext = {
    val conf = new SparkConf().setMaster("local[4]").setAppName(name)
    val ssc = new StreamingContext(conf, batchDuration)
    ssc.sparkContext.setLogLevel(logLevel)
    ssc
  }

  def sparkServerStreamingContext(name: String, batchDuration: Duration, logLevel: String = "INFO"): StreamingContext = {
    val conf = new SparkConf().setAppName(name)
    val ssc = new StreamingContext(conf, batchDuration)
    ssc.sparkContext.setLogLevel(logLevel)
    ssc
  }

  /**
    * 计算TopN的函数，最终版
    *
    * @param item     当前这个参加排序的数据
    * @param topNum   Top多少，如果Top5 这个参数就是5
    * @param topNList 用于指定在哪个数组中进行排序，
    *                 这个数组必须是已经进行过排序的数组，首次排序可以不输入此值
    *
    */
  def topN[T <: Comparable[T]](item: T, topNum: Int, topNList: util.List[T] = new util.ArrayList[T]): util.List[T] = {

    if (topNList.size() < topNum) {
      topNList.add(item)
      topNList.sort(new Comparator[T] {
        override def compare(o1: T, o2: T): Int = o2.compareTo(o1)
      })
      return topNList
    }

    // item = 12 | top5 = (20,15,11,9,1)
    for (i <- 0 until topNum if item.compareTo(topNList.get(i)) > 0) {
      // i = 2 | top5(3) = 9 | item = 10 | top5 = (20,15,11,9,1)
      for (j <- (i until topNum - 1).reverse) {
        //j = 2 | i = 2 | item = 10 | top5 = (20,15,11,9,9)
        //j = 3 | i = 2 | item = 10 | top5 = (20,15,11,11,9)
        topNList.set(j + 1, topNList.get(j))
      }
      //i = 2 | item = 12 | top5 = (20,15,12,11,9)
      topNList.set(i, item)
      return topNList
    }
    topNList
  }

}


