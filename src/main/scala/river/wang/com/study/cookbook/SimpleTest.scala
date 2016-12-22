package river.wang.com.study.cookbook

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import org.apache.spark.Logging
import river.wang.com.study.MyUtils

import scala.math.Ordering

/**
  * Created by wxx on 2016/12/12.
  */
object SimpleTest extends MyUtils with Logging {
  def main(args: Array[String]): Unit = {
    //    println(Array((1, 1), (2, 2), (3, 2), (4, 2), (5, 1))
    //      .sortWith((item: (Int, Int), item2: (Int, Int)) =>
    //        item._1.compareTo(item2._1) < 0
    //      ).mkString(","))

    //    val items = Array((0, 1), (1, 2), (2, 2), (3, 2), (4, 2), (5, 1))
    //    val (bucketIndex, index) = LocalTest.getIndexBySumBucket(5, items)
    //    println(bucketIndex + "___" + index)

    //    testParallizer()

    //    val ints = Array(1, 2, 3, 4, 6, 4, 3, 2, 1)
    //    var i = 0
    //    for (elem <- ints) {
    //      i = i ^ elem
    //    }
    //    println(i)



  }

  def testParallizer(): Unit = {
    val sc = sparkContext("LocalTest", "INFO", Some("local[5]"))
    val items = Seq((0, 1), (3, 2), (1, 2), (5, 1), (2, 2), (4, 2))
    val data = sc.parallelize(items, 3)

    sc.runJob(data, pickItem(2), Seq(0))

    while (true) {
      Thread.sleep(5 * 1000)
    }
    sc.stop()
  }

  def pickItem(index: Int) = (it: Iterator[(Int, Int)]) => {
    var result = 0
    var i = 1
    for (elem <- it if result == 0) {
      println(i + "-" + elem)
      if (i == index) {
        result = elem._2
      }
      i += 1
    }
    result
  }
}
