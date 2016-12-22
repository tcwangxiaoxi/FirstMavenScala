package river.wang.com.study.sparksql

import org.apache.spark.sql.SaveMode
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/30.
  */
object ParquetOps extends MyUtils {

  def main(args: Array[String]): Unit = {
    //    val ssc = sparkServerSQLContext("My DataFrameRDDTransformation App")
    val ssc = sparkSQLContext("My DataFrameRDDTransformation App")


    val rdd = ssc.sparkContext.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.txt")

    import ssc.implicits._
    val people = rdd.map(item => MyPeople(item.split(" ")(0), item.split(" ")(1).trim.toInt)).toDF()
    // An RDD of case class objects, from the previous example.

    // The RDD is implicitly converted to a DataFrame by implicits, allowing it to be stored using Parquet.
    people.write.mode(SaveMode.Overwrite).parquet("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.parquet")

    // Read in the parquet file created above. Parquet files are self-describing so the schema is preserved.
    // The result of loading a Parquet file is also a DataFrame.
    val parquetFile = ssc.read.parquet("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.parquet")

    //Parquet files can also be registered as tables and then used in SQL statements.
    parquetFile.registerTempTable("parquetFile")
    val teenagers = ssc.sql("SELECT name FROM parquetFile WHERE age >= 13 AND age <= 19")
    teenagers.map(t => "Name: " + t.getAs[String]("name")).collect().foreach(println)


  }

  case class MyPeople(name: String, age: Int)

}
