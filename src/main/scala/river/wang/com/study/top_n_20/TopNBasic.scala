package river.wang.com.study.top_n_20

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/19.
  */
object TopNBasic extends MyUtils {

  def main(args: Array[String]): Unit = {
    val sc = sparkContext("Top N Basic App!", "WARN")

    sc.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\basic-top-n.txt")
      .map(lines => (lines.toInt, lines)).sortByKey(ascending = false).map(_._2).take(5).foreach(println)

    sc.stop()

  }

}
