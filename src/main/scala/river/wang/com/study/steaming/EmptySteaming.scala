package river.wang.com.study.steaming

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Durations
import river.wang.com.study.MyUtils

import scala.collection.mutable.{HashSet, Stack}

/**
  * Created by wxx on 2016/11/10.
  */
object EmptySteaming extends MyUtils {
  def main(args: Array[String]): Unit = {
    val ssc = sparkStreamingContext("HelloSteaming", Durations.seconds(10))
    val textStream = ssc.socketTextStream("hadoop1", 9999)
    val countMap = textStream.flatMap(_.split(" ")).map((_, 1)).cache()

    countMap.reduceByKey(_ + _).transform(blockRdd => {
      if (checkRootRDDs(blockRdd, isNotEmptyRDDAction)) {
        blockRdd
      } else
        blockRdd.repartition(0)
    }).print()

    // 原始版本
    //    textStream.foreachRDD(blockRdd => {
    //      val countMap = blockRdd.flatMap(_.split(" ")).map((_, 1)).cache()
    //      countMap.reduceByKey(_ + _).collect().foreach(println)
    //    })

    ssc.start()
    ssc.awaitTermination()

  }

  def testEmpty(): Unit = {
    val sc = sparkContext("my first idea app")
    val a = sc.makeRDD(Array[Int](), 0)
    val b = sc.makeRDD(Array[Int](), 0)
    val c = a.map(2 * _).map((_, 1))
    val d = b.map((_, 1)).reduceByKey(_ + _)
    val x = c.leftOuterJoin(d)
    println(checkRootRDDs(x, isNotEmptyRDDAction))
  }

  /**
    * 遍历RDD，找到根节点，并依此指定的函数进行校验。
    *
    * @param rdd
    * @param rootFunc 操作的返回值，决定是否继续遍历，false为退出遍历，并校验失败
    */
  def checkRootRDDs(rdd: RDD[_], rootFunc: (RDD[_]) => Boolean): Boolean = {
    val visited = new HashSet[RDD[_]]
    val waitingForVisit = new Stack[RDD[_]]
    def visit(r: RDD[_]): Boolean = {
      if (!visited(r)) {
        visited += r
        val dependencies = r.dependencies
        if (dependencies.nonEmpty) {
          dependencies.map(dep => waitingForVisit.push(dep.rdd))
        } else {
          if (!rootFunc(r)) {
            return false
          }
        }
      }
      return true
    }
    waitingForVisit.push(rdd)
    while ( waitingForVisit.nonEmpty ) {
      if (!visit(waitingForVisit.pop())) {
        return false
      }
    }
    true
  }

  /**
    * 遍历根节点，并查看根节点是否有数据，
    * 是否有数据的来源是通过查看dependencies.length 是否大于 0 决定的
    *
    * @param rdd
    * @return
    */
  def isNotEmptyRDDAction(rdd: RDD[_]): Boolean = {
    rdd.partitions.length > 0
  }

}
