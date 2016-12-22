package river.wang.com.study.cookbook.reverseindex

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by wxx on 2016/12/13.
  */
object ReverseIndexUtil {

  def reverseIndex(sc: SparkContext, rdd: RDD[String]
                   , sparatorKey: String = " ", sparatorValues: String = "\t"): RDD[String] = {
    val kvInfo = rdd.flatMap(book => {
      val bookName = book.split(sparatorValues)(0)
      val words = book.split(sparatorValues)(1).split(sparatorKey)
      words.map((bookName, _))
    }).distinct()

    kvInfo.map(info => (info._2, info._1)).reduceByKey(_ + sparatorKey + _)
      .map(result => result._1 + sparatorValues + result._2)
  }
}
