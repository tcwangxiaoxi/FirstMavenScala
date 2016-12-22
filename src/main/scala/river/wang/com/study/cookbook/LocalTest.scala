package river.wang.com.study.cookbook

import org.apache.spark.{Logging, SparkContext}
import river.wang.com.study.MyUtils
import river.wang.com.study.cookbook.median.MedianUtil
import river.wang.com.study.cookbook.reverseindex.ReverseIndexUtil

/**
  * Created by wxx on 2016/12/12.
  */
object LocalTest extends MyUtils with Logging {
  def main(args: Array[String]): Unit = {
    val sc = sparkContext("LocalTest", "INFO", Some("local[5]"))
    // 中位数本地测试
    // MedianNumTest(sc, 1 to 20, 2, 3)

    //倒排索引本地测试
    ReverseIndexTest(sc, Array("book1\t1 3 5", "book2\t6 3 4", "book3\t9 1 7 8", "book1\t2 3 7"), 2)

    while (true) {
      Thread.sleep(5 * 1000)
    }
    sc.stop()
  }

  def ReverseIndexTest(sc: SparkContext, testArray: Seq[String], partitionSize: Int
                       , sparatorKey: String = " ", sparatorValues: String = "\t"): Unit = {
    val data = sc.makeRDD(testArray, partitionSize)
    val result = ReverseIndexUtil.reverseIndex(sc, data)
    result.collect().foreach(println)

  }

  def MedianNumTest(sc: SparkContext, testArray: Seq[Int], partitionSize: Int, keysPerBuchet: Int): Unit = {
    val rdd = sc.parallelize(testArray, partitionSize)
    val result = MedianUtil.getMedianNum(sc, rdd, keysPerBuchet)
    printf("中位数为：" + result)
  }
}
