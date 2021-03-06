package river.wang.com.study.secondary_sorting_19

import org.apache.spark.{SparkConf, SparkContext}
import river.wang.com.study.MyUtils

/**
  * 二次排序的具体步骤
  *
  * 1、按照Ordered和Serializable接口实现自定义排序的Key
  * 2、将要进行二次排序的文件加载进来生成<Key,Value>类型的RDD
  * 3、使用sortByKey基于自定义的Key进行二次排序
  * 4、去除掉排序的Key，只保留排序的结果
  *
  * Created by wxx on 2016/10/19.
  */
object SecondarySort extends MyUtils {
  def main(args: Array[String]): Unit = {

    val sc = sparkContext("my secondary sort app")

    val lines = sc.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\secondary-sort.txt")
    lines.map(lines => {
      val pair = lines.split(" ")
      val key = SecondarySortKey(pair(0).toInt, pair(1).toInt)
      (key, lines)
    }).sortByKey(ascending = false).map(_._2).collect().foreach(println)

    sc.stop()
  }
}
