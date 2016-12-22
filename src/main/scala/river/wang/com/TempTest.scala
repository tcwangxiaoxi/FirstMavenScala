package river.wang.com

import org.apache.spark.repl.Main
import river.wang.com.study.cookbook.websocket.listener.AsynchronousListenerBus
import river.wang.com.study.cookbook.websocket.validator.ValidateService

import scala.collection.mutable
import scala.collection.mutable._


/**
  * Created by wxx on 2016/10/19.
  */
object TempTest {
  def main(args: Array[String]): Unit = {
    /*val array = Array[(Int, (Int, String))](
      (1, (1, "a")),
      (1, (3, "b")),
      (1, (5, "c")),
      (2, (1, "a")),
      (2, (5, "c")),
      (3, (3, "c")),
      (3, (3, "a")),
      (2, (1, "b")),
      (4, (1, "a")),
      (3, (2, "b")),
      (3, (1, "d"))
    )

    topN(array.iterator, 2).foreach(println)*/
  }

  /**
    * 计算TopN的函数，
    *
    * @param topNList 每次遍历的结果存在这个集合中，需要用空的数据占位
    * @param item     当前这个参加排序的数据
    * @param topNum   Top多少，如果Top5 这个参数就是5
    */
  def topN(topNList: Array[(Int, String)], item: (Int, String), topNum: Int): Array[(Int, String)] = {
    val count = item._1
    val word = item._2
    // item = 12 | top5 = (20,15,11,9,1)
    for (i <- topNList.indices if count > topNList(i)._1) {
      // i = 2 | top5(3) = 9 | item = 10 | top5 = (20,15,11,9,1)
      for (j <- (i until topNum - 1).reverse) {
        //j = 2 | i = 2 | item = 10 | top5 = (20,15,11,9,9)
        //j = 3 | i = 2 | item = 10 | top5 = (20,15,11,11,9)
        topNList(j + 1) = topNList(j)
      }
      //i = 2 | item = 12 | top5 = (20,15,12,11,9)
      topNList(i) = (count, word)
      return topNList
    }
    topNList
  }

  /**
    * 获取 Map 中的topN
    * map格式如下：[要分组的值group Index，(要排序的值:count，与要排序的值关联的参数params)]
    *
    * @param iterator
    * @param topNum
    * @return
    */
  def topN(iterator: Iterator[(Int, (Int, String))], topNum: Int): Iterator[(Int, Int, String)] = {
    // 第一个参数是 累计的用于排序的数量
    // 第二个参数分别是基于统计的文字和基于统计的类别ID
    var tempMap = mutable.HashMap[Int, Array[(Int, String)]]()


    while (iterator.hasNext) {

      val item = iterator.next()
      val index = item._1

      val topNList = tempMap.get(index) match {
        case Some(list) => list
        case None =>
          // 添加占位数组，用于存放 TopN 结果
          tempMap += (index -> Array.fill(topNum)((0, "")))
          tempMap(index)
      }

      topN(topNList, item._2, topNum)
    }

    tempMap.flatMap(item => {
      val tempArray = ArrayBuffer[(Int, Int, String)]()
      item._2.foreach(tuple => {
        if (tuple._1 > 0) tempArray += ((item._1, tuple._1, tuple._2))
      })
      tempArray
    }).iterator
  }

  def top2ByInts(iterator: Iterator[(Int, (Int, String))], topNum: Int): Iterator[(Int, (Int, String))] = {

    // 第一个参数是 累计的用于排序的数量
    // 第二个参数分别是基于统计的文字和基于统计的类别ID
    var tempMap = mutable.HashMap[Int, Array[(String, Int)]]()

    while (iterator.hasNext) {

      val item = iterator.next()

      val index = item._2._1
      val count = item._1
      val word = item._2._2

      val topNList = tempMap.get(index) match {
        case Some(list) => list
        case None =>
          // 添加占位数组，用于存放 TopN 结果
          tempMap += (index -> Array.fill(topNum)(("", -1)))
          tempMap(index)
      }
      var isAdded = false
      // item = 12 | top5 = (20,15,11,9,1)
      for (i <- topNList.indices if count > topNList(i)._2 && !isAdded) {
        // i = 2 | top5(3) = 9 | item = 10 | top5 = (20,15,11,9,1)
        for (j <- (i until topNum - 1).reverse) {
          //j = 2 | i = 2 | item = 10 | top5 = (20,15,11,9,9)
          //j = 3 | i = 2 | item = 10 | top5 = (20,15,11,11,9)
          topNList(j + 1) = topNList(j)
        }
        //i = 2 | item = 12 | top5 = (20,15,12,11,9)
        topNList(i) = (word, count)
        isAdded = true
      }
    }

    val result = ArrayBuffer[(Int, (Int, String))]()

    tempMap.foreach(item => {
      val index = item._1
      val topNList = item._2
      topNList.foreach(item => {
        val count = item._2
        if (count > 0) {
          result += ((index, (count, item._1)))
        }
      })
    })
    result.iterator
  }

  def mapOpsTest() = {
    val map = mutable.HashMap[Int, Int]()
    val item = map.get(1) match {
      case Some(a) =>
        println("null")
        a
      case _ => println("null1")
    }
    println(item)

    map(1) = 3
    map(4) = 5
    println(map.mkString(","))
  }

  def func_() = {

    def test(items: Iterator[Int], func: Iterator[Int] => Long): Long = {
      func(items)
    }

    def getIteratorSize[T](iterator: Iterator[T]): Long = {
      var count = 0L
      while (iterator.hasNext) {
        count += 1L
        iterator.next()
      }
      count
    }

    val a = Array(1, 3, 4, 5, 6)

    println(test(a.iterator, getIteratorSize))
  }


}
