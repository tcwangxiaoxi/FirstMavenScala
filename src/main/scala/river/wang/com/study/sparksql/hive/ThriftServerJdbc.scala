package river.wang.com.study.sparksql.hive

import java.sql.DriverManager

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/1.
  */
object ThriftServerJdbc extends MyUtils {

  def main(args: Array[String]): Unit = {
    //    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val connection = DriverManager.getConnection("jdbc:hive2://hadoop1:10000/default", "wxx", "123")
    val result = connection.createStatement().executeQuery("select count(0) from test")
    result.next()
    val count = result.getLong(1)

    println("println(count)" + count)
  }
}
