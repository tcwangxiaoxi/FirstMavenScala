package river.wang.com.study.sparksql

import java.sql.DriverManager

import river.wang.com.study.MyUtils

import scala.collection.mutable.ArrayBuffer

/**
  *  尝试在通过 RDD的 mappartitions 函数，在每个Task中连接一次数据库并执行任务；
  * Created by wxx on 2016/10/31.
  */
object MySQLOps2 extends MyUtils {
  def main(args: Array[String]): Unit = {
    val ssc = sparkServerSQLContext("My MySQLOps2 App")
    val rdd = ssc.sparkContext.textFile("hdfs://hadoopHa/user/wxx/sogou/SogouQDemoDatas")
    //    val rdd = ssc.sparkContext.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\people.txt")
    val count = rdd.mapPartitions(items => {

      val connection = DriverManager.getConnection("jdbc:mysql://192.168.246.3:3306/BOOT_DEMO", "root", "123")
      val result = connection.createStatement().executeQuery("select count(0) from ACCOUNT")
      result.next()
      val count = result.getLong(1)

      //println("println(items.length)" + items.length)
      println("println(count)" + count)

      val array = ArrayBuffer[String]()

      items.foreach(item => {
        array += item
      })

      for (i <- 1 to count.toInt) {
        array += i.toString
      }
      println("println(array.length)" + array.length)
      array.iterator
    }).count()

    println(count)

  }
}
