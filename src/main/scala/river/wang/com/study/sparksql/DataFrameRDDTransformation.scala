package river.wang.com.study.sparksql

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/30.
  */
object DataFrameRDDTransformation extends MyUtils {
  def main(args: Array[String]): Unit = {
    //    val ssc = sparkServerSQLContext("My DataFrameRDDTransformation App")
    val ssc = sparkSQLContext("My DataFrameRDDTransformation App")

    import ssc.implicits._

    val rdd = ssc.sparkContext.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\group-top-n.txt")

    val df = rdd.map(item => MyPerson(item.split(" ")(0), item.split(" ")(1).toInt)).toDF()

    df.registerTempTable("person")

    ssc.sql("select * from person where score > 40").map(t => "Name: " + t.getAs[String]("name")).collect().foreach(println)


  }
  case class MyPerson(name: String, score: Int)


}


