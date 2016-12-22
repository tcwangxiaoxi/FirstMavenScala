package river.wang.com.study.cookbook.websocket

import kafka.serializer.StringDecoder
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.kafka.KafkaUtils
import river.wang.com.study.MyUtils

object SalesStatistics extends MyUtils {

  def main(args: Array[String]): Unit = {
    val (appName, duration) = ("SalesStatistics", Durations.seconds(30))
    val ssc = sparkStreamingContext(appName, duration)

    ssc.checkpoint(s"hdfs://hadoopHa/wxx/spark/checkpoint/${appName}")

    val listingSectors = Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5)
    ssc.sparkContext.broadcast(listingSectors)

    /*val orderStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
      Map("metadata.broker.list" -> "hadoop2:9092,hadoop3:9092,hadoop4:9092", "group.id" -> "testGroup"),
      Set("SalesStatistics"))
    //获取数据的访问流
    val kvStream = orderStream.map(item => {
      val params = item._2.split(",")
      (listingSectors.getOrElse(params(0), 0), params(1).toDouble)
    })*/

    val orderStream = ssc.socketTextStream("hadoop1", 9999)
    //获取数据的访问流
    val kvStream = orderStream.map(item => {
      val params = item.split(",")
      (listingSectors.getOrElse(params(0), 0), params(1).toDouble)
    })

    val updateFunc = (currentItem: Seq[(Double)], prePrice: Option[(Double, Double)]) => {
      val values = currentItem.mkString(",")
      println(s"currentItem.size=${currentItem.size},currentItem=${values}")
      val oldVal = prePrice.getOrElse((0.0, 0.0))._2
      println(s"oldVal=${oldVal},currentItem.sum=${currentItem.sum}")
      Some((oldVal, currentItem.sum))
    }

    //获取访问量的变化值(key,访问量) -> (key,变化的访问量,1)
    val changeInfos = kvStream.updateStateByKey[(Double, Double)](updateFunc).map(item => {
      println(s"item._1=${item._1},item._2=${item._2}")
      val diff = item._2._2 - item._2._1
      (item._1, (item._2._2, diff, if (diff > 0) 1 else -1))
    })

    val reduceFunc = (reduced: (Double, Double, Int), current: (Double, Double, Int)) => {
      (reduced._1 + current._1, 0.0, reduced._3 + current._3)
    }

    val invReduceFunc = (reduced: (Double, Double, Int), lost: (Double, Double, Int)) => {
      println(s"reduced=${reduced},lost=${lost}")
      (reduced._1 - lost._1, 0.0, reduced._3 - lost._3)
    }

    // 统计窗口时间
    val result = changeInfos.reduceByKeyAndWindow(reduceFunc, invReduceFunc, Durations.seconds(60), Durations.seconds(30))

    result.print()

    result.filter(_._2._3 > 0).map(item => (item._2._3, item._1)).foreachRDD(rdd => {
      /*def getLastNumFromPreBucket[T](data: RDD[T])(implicit ordering: Ordering[T]): Array[T] = {
        data.takeOrdered(3)(ordering.reverse)
      }
      val resultData = getLastNumFromPreBucket(rdd)*/
      val resultData = rdd.takeOrdered(3)(implicitly[Ordering[(Int, Int)]].reverse)
      // 取出最佳最多次的打印
      println("==========" + resultData.mkString(",") + "==========")
    })

    ssc.start()
    ssc.awaitTerminationOrTimeout(121 * 1000)
  }

}
