package river.wang.com.study.sparksql.catalyst

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import river.wang.com.study.MyUtils

/**
  * Spark 1.5.x 开始提供了大量的内置函数，例如
  * agg, max, mean, min, sum, avg, expload, size, sort_array, day, to_date, abs, acros, asin, atan
  * 总体上而言内置函数包含了五大基本类型：
  * 1、聚合函数，例如：countDistinct、sumDistinct等；
  * 2、集合函数，例如：sort_array、explode等；
  * 3、日期、时间函数，例如：hour、quarter、next_day等；
  * 4、开窗函数，例如：rowNumber等；
  * 5、字符串函数，例如：concat、format_number、regexp_extract等；
  * 6、数学函数，例如：asin、atan、sqrt、tan、round等；
  * 7、其它函数，例如：isNaN，sha，random、callUDF等；
  * Created by wxx on 2016/10/31.
  */
object SparkSqlLocalTest extends MyUtils {
  def main(args: Array[String]): Unit = {
    try {
      val sc = sparkSQLContext("My SparkOnHive App!")
      val userData = Array(
        "2016-3-27,001,http://spark.apache.org/,1000",
        "2016-3-27,001,http://hadoop.apache.org/,2000",
        "2016-3-27,002,http://hive.apache.org/,7000",
        "2016-3-28,001,http://scala.apache.org/,6000",
        "2016-3-28,002,http://sqoop.apache.org/,4000",
        "2016-3-28,001,http://parquet.apache.org/,2000",
        "2016-3-28,003,http://kafka.apache.org/,2000",
        "2016-3-28,004,http://mapreduce.apache.org/,4000")

      val datas = sc.sparkContext.parallelize(userData).map(line => {
        val items = line.split(",")
        Row(items(0), items(1).toInt, items(2), items(3).toLong)
      })

      val schema = StructType(Array(
        StructField("date", StringType, nullable = true),
        StructField("id", IntegerType, nullable = true),
        StructField("url", StringType, nullable = true),
        StructField("amount", LongType, nullable = true)
      ))

      sc.createDataFrame(datas, schema).registerTempTable("order1")

//      sc.sql("select amount from order1 where id = '002'").foreach(println)
        sc.sql("show tables").foreach(println)

    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
