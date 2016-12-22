package river.wang.com.study.secondary_sorting_19

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/19.
  */
object SortBasic extends MyUtils {
  def main(args: Array[String]): Unit = {

    val sc = sparkContext("my secondary sort app")

    sc.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\secondary-sort.txt").flatMap(_.split(" ")).map((_, 1))
      .reduceByKey(_ + _).map(pair => (pair._2, pair._1))
      .sortByKey(false).map(pair => (pair._2, pair._1))
      .collect.foreach(println)

    sc.stop()
  }
}
