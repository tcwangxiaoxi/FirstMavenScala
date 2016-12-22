package river.wang.com.study.cookbook.median

import java.util.Comparator

import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}
import river.wang.com.study.cookbook.SortSample

import scala.math.Ordering

/**
  * Created by wxx on 2016/12/13.
  */
object MedianUtil extends Logging {

  def getMedianNum(sc: SparkContext, rdd: RDD[Int], keysPerBuchet: Int): Double = {
    if (rdd.partitions.size == 0) {
      return 0
    }
    val data = rdd
      .map(sum => (sum / keysPerBuchet, (1, sum))).cache()
    val countByKey = data.aggregateByKey(0)((zero: Int, item: (Int, Int)) => {
      zero + item._1
    }, _ + _).collect()
    val total = countByKey.map(_._2).sum
    val medianIndex = total / 2 + 1

    // 防止顺序由于Shuffle变乱，这里需要重新按顺序累加
    SortSample.quickSort(countByKey, new Comparator[(Int, Int)] {
      override def compare(o1: (Int, Int), o2: (Int, Int)): Int = {
        o1._1.compareTo(o2._1)
      }
    })

    val (bucketIndex, index) = getIndexBySumBucket(medianIndex, countByKey)
    logInfo(s"==========================bucketIndex:${bucketIndex},index:${index}")

    //最原始的API级别的调用，获取分桶中的数据
    val resultIter = data.filter(_._1 == bucketIndex).takeOrdered(index)
    data.unpersist()

    val oddResult = resultIter.last._2._2
    val result = if (total % 2 == 1) oddResult
    else {
      var preValue = 0
      if (resultIter.length > 1) {
        val list = resultIter.toList
        preValue = list(list.size - 2)._2._2
      } else {
        /* 这里表示如果是偶数，而且是在该分桶中的第一个，
         * 则这里取不到上一个桶的数据，则还需要去取上一个桶的最后一个值
         */
        def getLastNumFromPreBucket[T](data: RDD[T])(implicit ordering: Ordering[T]): Array[T] = {
          data.takeOrdered(1)(ordering.reverse)
        }

        val resultIter = getLastNumFromPreBucket(data.filter(_._1 == bucketIndex - 1))
        preValue = resultIter.head._2._2
      }
      (oddResult + preValue) / 2.0
    }
    logInfo("计算成功，中位数为：" + result)
    result
  }


  /**
    * 根据指定的列表，以及数量的一半，找到中位数所在的位置
    *
    * @param medianIndex 中位数的总偏移量
    * @param countByKey  一个根据桶分区并以个数为值的列表。
    * @return 返回列表中中位值所在的桶以及在该桶所在的位置
    */
  def getIndexBySumBucket(medianIndex: Int, countByKey: Array[(Int, Int)]): (Int, Int) = {
    // 记录当前循环到的索引
    var preSumByBucket = 0
    // 记录找到的桶的索引
    var bucketIndex = -1

    var isBroke = false
    for (i <- 0 to countByKey.length - 1 if !isBroke) {
      val curSumByBucket = preSumByBucket + countByKey(i)._2
      if (curSumByBucket >= medianIndex) {
        bucketIndex = countByKey(i)._1
        isBroke = true
      } else
        preSumByBucket = curSumByBucket
    }
    if (!isBroke) {
      throw new Exception("Impossible")
    }
    //（桶的索引，桶内位置的索引）
    (bucketIndex, medianIndex - preSumByBucket)
  }
}
