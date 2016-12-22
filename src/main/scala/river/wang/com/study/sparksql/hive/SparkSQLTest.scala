package river.wang.com.study.sparksql.hive

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{SQLContext, SaveMode}
import river.wang.com.study.MyUtils

import scala.collection.mutable.ArrayBuffer


/**
  * Created by wxx on 2016/11/3.
  */
object SparkSQLTest extends MyUtils {
  def main(args: Array[String]): Unit = {
    var sc: HiveContext = null
    try {
      sc = sparkServerHiveContext("My Test Parquet")
      //初始化UDF
      registeUDF(sc)
      //把 txt 文件 转换为 Parquet 文件
      //    toParquet(sc)
      //格式化 Parquet 文件
      //    readParquet(sc)
      //分析测试数据
      analyzeData(sc)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      sc.sparkContext.stop()
    }
  }

  def analyzeData(sqlContext: SQLContext): Unit = {
    //    val df = sqlContext.read.parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_formatted_finall/final.parquet").registerTempTable("sogou")
    //    sqlContext.sql("SELECT concat(word,searchindex) as key ,count(0) AS count FROM sogou GROUP BY searchindex,word ")
    //    .write.parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_formatted_finall/temp.parquet")

    val df = sqlContext.sql("SELECT searchindex, word FROM sogou")
    val countRdd = df.map(row => ((row.getAs[Int]("searchindex"), row.getAs[String]("word")), 1)).reduceByKey(_ + _).map(item => (item._1._1, (item._2, item._1._2)))

    val topNum = 3
    countRdd.aggregateByKey(Array.fill(topNum)((0, "")), 1)((lastResult, item) => topN(lastResult, item, topNum),
      (lastResult, curResult) => {
        val i = curResult.iterator
        while (i.hasNext) {
          topN(lastResult, i.next(), topNum)
        }
        lastResult
      }).sortByKey().flatMap(item => {
      val flatItems = ArrayBuffer[(Int, Int, String)]()
      item._2.map(flatArr => {
        if (flatArr._1 > 0)
          flatItems += ((item._1, flatArr._1, flatArr._2))
      })
      flatItems
    }).collect().foreach(item => println("index:" + item._1 + ",count:" + item._2 + ",word:" + item._3))


    //countRdd.partitionBy(new Partitioner {override def numPartitions: Int = 3; override def getPartition(key: Any): Int = if(key.toString.startsWith("A")) 0 else if (key.toString.startsWith("M")) 1 else 2}).coalesce(2).count()

    //    val people = sqlContext.sparkContext.textFile("hdfs://hadoopHa/wxx/spark/examples/src/main/resources/people.txt")
    //      .map(item =>(item.head,1)).sortBy(false)


    //    val sumRdd = kvRdd.repartition(10).map(item => ((item._1, item._2), 1)).reduceByKey(_ + _).map(row => (row._2, row._1))
    //    val sortRdd = sumRdd.sortByKey(ascending = false)
    //    val sumRdd.aggregateByKey()
    //    val sortRdd = sumRdd.mapPartitions(iterator => {
    //      topN(iterator)
    //    })
    //    val resultRdd = sortRdd.saveAsTextFile("hdfs://hadoopHa/wxx/spark/output/test2")


    //    sc.sql("SELECT key ,row_number() OVER ( PARTITION BY key ORDER BY count DESC ) rank FROM temp1").show()

    //    val result = sc.sql("select searchindex, word, rank " +
    //      "( select searchindex,word," +
    //      "row_number() OVER (PARTITION BY tmp1.searchindex,tmp1.word ORDER BY tmp1.count DESC) as rank " +
    //      "from (SELECT searchindex,word ,count(0) AS count FROM sogou GROUP BY searchindex,word) tmp1 " +
    //      ") tmp2" +
    //      "where tmp2.rank <= 3").show()


  }

  /**
    * 计算TopN的函数，
    *
    * @param topNList 每次遍历的结果存在这个集合中，需要用空的数据占位
    * @param item     当前这个参加排序的数据
    * @param topNum   Top多少，如果Top5 这个参数就是5
    */
  def topN(topNList: Array[(Int, String)], item: (Int, String), topNum: Int): Array[(Int, String)] = {
    val count = item._1
    val word = item._2
    // item = 12 | top5 = (20,15,11,9,1)
    for (i <- topNList.indices if count > topNList(i)._1) {
      // i = 2 | top5(3) = 9 | item = 10 | top5 = (20,15,11,9,1)
      for (j <- (i until topNum - 1).reverse) {
        //j = 2 | i = 2 | item = 10 | top5 = (20,15,11,9,9)
        //j = 3 | i = 2 | item = 10 | top5 = (20,15,11,11,9)
        topNList(j + 1) = topNList(j)
      }
      //i = 2 | item = 12 | top5 = (20,15,12,11,9)
      topNList(i) = (count, word)
      return topNList
    }
    topNList
  }

  def correctionDatas(sc: SQLContext): Unit = {
    // 处理错误数据
    val correctionDf = sc.sql("select " +
      "createtime," +
      "userid," +
      "concat(word,ranking) as word," +
      "strToInt(split(searchindex,' ',0)) as ranking," +
      "strToInt(split(searchindex,' ',1)) as searchindex, " +
      "url " +
      "from sogou " +
      "where strLen(url) > 0 ")

    correctionDf.write.mode(SaveMode.Overwrite).parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_formatted_finall/final.parquet")
  }

  /**
    * 保存剩下的正确数据
    *
    * @param sc
    */
  def otherDatas(sc: SQLContext): Unit = {

    val sql = "select " +
      "createtime," +
      "userid," +
      "word," +
      "strToInt(split(ranking,' ',0)) as ranking," +
      "strToInt(split(ranking,' ',1)) as searchindex, " +
      "searchindex as url " +
      "from sogou " +
      "where strLen(url) = 0 "
    val restOfDf = sc.sql(sql)
    restOfDf.write.mode(SaveMode.Append).parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_formatted_finall/final.parquet")
  }

  def readParquet(sc: SQLContext): Unit = {
    sc.read.parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_row6/").registerTempTable("sogou")
    //处理并保存错误的数据
    correctionDatas(sc)

    // 保存剩下的正确数据
    otherDatas(sc)

  }

  def toParquet(sc: SQLContext): Unit = {

    val data = sc.sparkContext.textFile("hdfs://hadoopHa/user/wxx/sogou-org/SogouQ_reduced_UTF8.txt")
      .map(item => {
        val row = item.split("\t")
        if (row.length > 5) {
          SougoData(row(0), row(1), row(2), row(3), row(4), row(5))
        } else
          SougoData(row(0), row(1), row(2), row(3), row(4), "")
      }
      )

    import sc.implicits._

    val df = data.toDF()

    df.write.parquet("hdfs://hadoopHa/user/wxx/sogou_parquet_row6/")
  }

  def registeUDF(sc: SQLContext): Unit = {
    sc.udf.register("strLen", (input: String) => input.length)
    sc.udf.register("strToInt", (input: String) => input.toInt)

    sc.udf.register("split", (input: String, split: String, index: Int) => input.split(split)(index))

    // 匹配是否为数字
    sc.udf.register("isNumber", (input: String) => {
      def isIntByRegex(s: String) = {
        val pattern = """^(\d+)$""".r
        s match {
          case pattern(_*) => true
          case _ => false
        }
      }
      isIntByRegex(input)
    })
  }

}

case class SougoData(createtime: String, userid: String, word: String, ranking: String, searchindex: String, url: String)
