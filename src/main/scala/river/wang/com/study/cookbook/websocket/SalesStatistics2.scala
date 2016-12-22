package river.wang.com.study.cookbook.websocket

import org.apache.spark.streaming.Durations
import river.wang.com.study.steaming.HelloSteaming._

object SalesStatistics2 {

  case class SaleInfo(id: Int, summary: Double, riseAmount: Double, var rank: Int = 1)

  def main(args: Array[String]): Unit = {
    val ssc = sparkStreamingContext("HelloSteaming", Durations.seconds(15))

    val listingSectors = Map("a" -> 1, "b" -> 2, "c" -> 3)
    ssc.sparkContext.broadcast(listingSectors)

    val textStream = ssc.socketTextStream("hadoop1", 9999)

    //获取数据的访问流
    val kvStream = textStream.map(item => {
      val params = item.split(",")
      (listingSectors.getOrElse(params(0), 0), params(1).toDouble)
    })

    //获取访问量的变化值(key,访问量) -> (key,变化的访问量,1)
    val changeInfos = kvStream.updateStateByKey[(Double, Double)]((currentItem: Seq[Double], prePrice: Option[(Double, Double)]) => {
      val curprice = currentItem.sum
      val x = prePrice match {
        case Some(p) =>
          Some((p._2, curprice))
        case _ => Some((curprice, curprice))
      }
      x
    }).map(item => (item._1, SaleInfo(item._1, item._2._2 + item._2._1, item._2._2 - item._2._1)))

    val reduceFunc = (reduced: SaleInfo, current: SaleInfo) => {
      if (current.riseAmount > 0) {
        (reduced.summary + current.summary, 0, reduced.rank + current.rank)
      } else {
        (reduced.summary + current.summary, 0, reduced.rank - current.rank)
      }
    }

    val invReduceFunc = (reduced: (SaleInfo), current: (SaleInfo)) => {
      if (current.riseAmount > 0) {
        (reduced.summary + current.summary, 0, reduced.rank - current.rank)
      } else {
        (reduced.summary + current.summary, 0, reduced.rank + current.rank)
      }
    }

    // 统计窗口时间
    //    val result = changeInfos.reduceByKeyAndWindow(reduceFunc, invReduceFunc, Durations.seconds(60), Durations.seconds(15))
    //
    //    result.filter(_._2._2 > 0).foreachRDD(rdd => {
    //      rdd.
    //    })
  }
}
