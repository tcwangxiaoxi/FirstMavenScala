package river.wang.com.study.top_n_20

import com.google.common.collect.Iterators
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/19.
  */
object TopNGroup extends MyUtils {

  def main(args: Array[String]): Unit = {
    val sc = sparkContext("Group Top N App!", "WARN")

    sc.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\group-top-n.txt")
      .map(lines => {
        val pair = lines.split(" ")
        (pair(0), pair(1).toInt)
      }).groupByKey().map(pair => (pair._1, top5ByInts(pair._2.iterator))).collect().foreach(pair => println(pair._1 + ":" + pair._2.mkString(",")))

    sc.stop()
  }

  def top5ByInts(iterator: Iterator[Int]): Array[Int] = {

    val top5 = Array.fill(5)(Int.MinValue)
    while ( iterator.hasNext ) {
      // item = 12 | top5 = (20,15,11,9,1)
      val item = iterator.next()
      var isAdded = false
      for (i <- top5.indices if item > top5(i) && !isAdded) {
        // i = 2 | top5(3) = 9 | item = 10 | top5 = (20,15,11,9,1)
        for (j <- (i to 4).reverse if j != i) {
          //j = 4 | i = 2 | item = 10 | top5 = (20,15,11,9,1)
          //j = 3 | i = 2 | item = 10 | top5 = (20,15,11,1,9)
          top5(j) = top5(j - 1)
        }
        //i = 2 | item = 10 | top5 = (20,15,1,11,9)
        top5(i) = item
        isAdded = true
      }
    }
    top5.filter(_ > Int.MinValue)
  }
}
