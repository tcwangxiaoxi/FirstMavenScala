package river.wang.com

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Hello world!
  *
  */
object App {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf
    conf.setAppName("my first idea app").setMaster("local")
    //    conf.setMaster("spark://hadoop1:7077")

    val sc = new SparkContext(conf)

    //    val lines = sc.textFile("C:\\WorkSpaceIdea\\FirstMavenScala\\test-datas\\test.txt")
    val lines = sc.makeRDD(Seq(1, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5))
    lines.map((_, 1)).map((_, 1)).reduceByKey(_ + _)
      .map(result => (result._2, result._1))
      .sortByKey(ascending = false, 1)
      .map(result => (result._2, result._1))
      .foreach(result => println(result._1 + " : " + result._2))

    sc.stop()
  }
}
