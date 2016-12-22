package river.wang.com.study.sparksql.hive

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/10/31.
  */
object SparkOnHive extends MyUtils {
  def main(args: Array[String]): Unit = {
    try {
      val hc = sparkHiveContext("My SparkOnHive App!")
      hc.sql("use default")
      hc.sql("DROP TABLE IF EXISTS people")
      hc.sql("CREATE EXTERNAL TABLE IF NOT EXISTS " +
        "people(" +
        " name STRING, " +
        " age INT" +
        ") ROW FORMAT DELIMITED FIELDS TERMINATED BY ','" +
        "LINES TERMINATED BY '\\n'" +
        "LOCATION '/wxx/spark/examples/src/main/resources/people'")
      hc.sql("SELECT * FROM people").show

      hc.sql("DROP TABLE IF EXISTS peopleScore")
      hc.sql("CREATE EXTERNAL TABLE IF NOT EXISTS " +
        "peopleScore(" +
        " name STRING, " +
        " score INT" +
        ") ROW FORMAT DELIMITED FIELDS TERMINATED BY ','" +
        "LINES TERMINATED BY '\\n'" +
        "LOCATION '/wxx/spark/examples/src/main/resources/peopleScore'")
      hc.sql("SELECT * FROM peopleScore").show

      val resultDF = hc.sql("SELECT p.name,p.age,s.score " +
        "FROM people p join peopleScore s ON p.name = s.name " +
        "WHERE s.score > 90 ")

      hc.sql("DROP TABLE IF EXISTS peopleScoreResult")
      resultDF.write.saveAsTable("peopleScoreResult")
      resultDF.show()
      hc.sql("SELECT * FROM peopleScoreResult").show

    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
