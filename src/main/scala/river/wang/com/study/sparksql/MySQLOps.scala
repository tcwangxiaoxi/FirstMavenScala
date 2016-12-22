package river.wang.com.study.sparksql

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/31.
  */
object MySQLOps extends MyUtils {
  def main(args: Array[String]): Unit = {
    val ssc = sparkServerSQLContext("My MySQLOps App")
    //    val ssc = sparkSQLContext("My MySQLOps App")

    val jdbcDF1 = ssc.read.format("jdbc").options(
      Map("url" -> "jdbc:mysql://192.168.246.3:3306/netty-chat",
        "dbtable" -> "player",
        "user" -> "root",
        "password" -> "123"
      )
    ).load().registerTempTable("player")

    val jdbcDF2 = ssc.read.format("jdbc").options(
      Map("url" -> "jdbc:mysql://192.168.246.3:3306/BOOT_DEMO",
        "dbtable" -> "ACCOUNT",
        "user" -> "root",
        "password" -> "123"
      )
    ).load().registerTempTable("account")

    val result = ssc.sql("select * from player p inner join account a on p.playerId = a.ID where playerId =1")

    result.show()


  }
}
