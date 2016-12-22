package river.wang.com.study.sparksql

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/30.
  */
object DataFrameRDDTransformationProgrammatically extends MyUtils {
  def main(args: Array[String]): Unit = {
    val ssc = sparkServerSQLContext("My DataFrameRDDTransformationProgrammatically App")
    val rdd = ssc.sparkContext.textFile("hdfs://hadoopHa/wxx/spark/examples/src/main/resources/people.txt")

    //    val ssc = sparkSQLContext("My DataFrameRDDTransformationProgrammatically App")
    //    val rdd = ssc.sparkContext.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.txt")

    val rowRDD = rdd.map(item => Row(item.split(" ")(0), item.split(" ")(1).trim.toInt))

    val schema = StructType(Array(
      StructField("name", StringType, nullable = true),
      StructField("age", IntegerType, nullable = true)
    ))

    val df = ssc.createDataFrame(rowRDD, schema)

    df.registerTempTable("person")

    ssc.sql("select * from person where age > 20").map(t => "Name: " + t.getAs[String]("name")).collect().foreach(println)


  }

}


