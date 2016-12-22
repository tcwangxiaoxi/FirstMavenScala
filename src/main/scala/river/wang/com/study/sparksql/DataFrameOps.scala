package river.wang.com.study.sparksql

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/29.
  */
object DataFrameOps extends MyUtils {

  def main(args: Array[String]): Unit = {
    val ssc = sparkSQLContext("My First Spark SQL")
    val df = ssc.read.json("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.json")

    //    val ssc = sparkServerSQLContext("My First Spark SQL")
    //    val df = ssc.read.json("hdfs://hadoopHa/wxx/spark/examples/src/main/resources/people.json")

    df.show()
    df.printSchema()
    df.select("name").show()
    df.select(df("name"), df("age") + 10).show()
    df.filter(df("age") > 10).show()
    df.groupBy("age").count().show()


  }
}
