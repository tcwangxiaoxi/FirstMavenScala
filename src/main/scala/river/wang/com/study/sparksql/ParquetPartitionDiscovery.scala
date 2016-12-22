package river.wang.com.study.sparksql

import org.apache.spark.sql.SaveMode
import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/30.
  */
object ParquetPartitionDiscovery extends MyUtils {

  def main(args: Array[String]): Unit = {
    val ssc = sparkSQLContext("My ParquetPartitionDiscovery App")

    import ssc.implicits._

    // Create a simple DataFrame, stored into a partition directory
    val df1 = ssc.sparkContext.makeRDD(1 to 5).map(i => (i, i * 2)).toDF("single", "double")
    df1.write.mode(SaveMode.Overwrite).parquet("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\parquetPartition\\test_table\\key=1")
    // Create another DataFrame in a new partition directory,
    // adding a new column and dropping an existing column
    val df2 = ssc.sparkContext.makeRDD(6 to 10).map(i => (i, i * 3)).toDF("single", "triple")
    df2.write.mode(SaveMode.Overwrite).parquet("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\parquetPartition\\test_table\\key=2")

    // Read the partitioned table
    val df3 = ssc.read.option("mergeSchema", "true")
      .parquet("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\parquetPartition\\test_table")
    df3.printSchema()
    df3.show()

    df3.registerTempTable("number")

    ssc.sql("select * from number where single > 3").show()


  }

}
