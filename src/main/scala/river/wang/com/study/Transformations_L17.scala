package river.wang.com.study

import org.apache.spark.test.TimeoutJobTrack
import org.apache.spark.{Logging, SparkContext, SparkException}

/**
  * 常用的Spark Transformation案例
  *
  * Created by wxx on 2016/10/18.
  */
object Transformations_L17 extends MyUtils with Logging {
  def main(args: Array[String]): Unit = {

    val sc = sparkContext("my first idea app", "INFO", Some("local[5]"))

    /*mapTransformation(sc)

    filterTransformation(sc)

    flatMapTransformation(sc)

    groupByKeyTransformation(sc)

    reduceByKeyTransformation(sc)

    joinTransformation(sc)

    coGroupTransformation(sc)

    coGroupTest(sc)

    stopTest(sc)*/

    sortByKey(sc)

    while (true) {
      Thread.sleep(5 * 1000)
    }

    sc.stop()
  }

  def mapTransformation(sc: SparkContext): Unit = {
    val numbers = sc.parallelize(1 to 10)
    numbers.map(2 * _).collect().foreach(println)
  }

  def filterTransformation(sc: SparkContext): Unit = {
    val numbers = sc.parallelize(1 to 20)
    numbers.filter(_ % 2 == 0).collect().foreach(println)
  }

  def flatMapTransformation(sc: SparkContext): Unit = {
    val words = Array("Scala Spark", "Java Hadoop", "Java Tachyon")
    sc.parallelize(words).flatMap(_.split(" "))
      .collect().foreach(println)
  }

  def groupByKeyTransformation(sc: SparkContext): Unit = {
    val data = Array((100, "Spark"), (80, "Java"), (70, "Hadoop"), (100, "Tachyon"))
    sc.parallelize(data).groupByKey()
      .collect().foreach(println)
  }

  def reduceByKeyTransformation(sc: SparkContext): Unit = {
    val data = Array((100, "Spark"), (80, "Java"), (70, "Hadoop"), (100, "Tachyon"))
    sc.parallelize(data).reduceByKey(_ + " : " + _)
      .collect().foreach(println)
  }

  def joinTransformation(sc: SparkContext): Unit = {
    val studentNames = Array((1, "Spark"), (2, "Tachyon"), (3, "Hadoop"))

    val studentScores = Array((1, 100), (2, 95), (3, 65))

    val names = sc.parallelize(studentNames)
    val scores = sc.parallelize(studentScores)
    names.join(scores).collect().foreach(println)
  }

  def coGroupTransformation(sc: SparkContext): Unit = {
    val studentNames = Array((1, "Spark"), (1, "Hive"), (2, "Tachyon"), (3, "Hadoop"), (3, "Scala"))
    val studentScores = Array((1, 100), (1, 80), (2, 95), (2, 65), (3, 65))

    val names = sc.parallelize(studentNames)
    val scores = sc.parallelize(studentScores)
    names.cogroup(scores).collect().foreach(println)
  }

  def coGroupTest(sc: SparkContext): Unit = {
    val studentNames = Array((1, "Spark"), (1, "Hive"), (1, "Tachyon"), (1, "Hadoop"), (1, "Scala"), (1, "Scala"))
    val studentScores = Array((1, 100), (1, 80), (2, 95), (2, 65), (3, 65))

    val names = sc.parallelize(studentNames, 100)
    val scores = sc.parallelize(studentScores, 3)
    names.cogroup(scores).collect().foreach(println)
  }


  def stopTest(sc: SparkContext): Unit = {
    val num = sc.parallelize(1 to 10, 2)


    TimeoutJobTrack(sc).start()

    /*ThreadUtils.newFixedThreadPool(1, "new-thread").submit(new Runnable {
      override def run(): Unit = {
        num.map(item => Thread.sleep(60 * 60 * 1000)).foreach(println)
      }
    })*/

    try {
      num.map(item => Thread.sleep(60 * 60 * 1000)).foreach(println)
    } catch {
      case e: SparkException => logInfo(e.getMessage, e)
      case exp: Exception => throw exp
    }

    for (i <- 1 to 5) {
      num.map(_ + i).collect().foreach(println)
    }
  }

  def sortByKey(sc: SparkContext): Unit = {
    val studentScores = "1,2,3,4,5,6,8,9,11,12,34"

    sc.parallelize(studentScores.split(","), 3).map(item => (item.toInt, item)).sortByKey()
      .collect().foreach(println)
  }
}
