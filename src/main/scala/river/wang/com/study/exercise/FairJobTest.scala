package river.wang.com.study.exercise

import com.esotericsoftware.kryo.Kryo
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap
import org.apache.curator.utils.ThreadUtils
import org.apache.spark.serializer.KryoRegistrator
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{Logging, SparkConf, SparkContext}

/**
  * Created by wxx on 2016/12/10.
  */
object FairJobTest extends Logging {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    conf.setAppName("FairJobTest")
    conf.set("spark.scheduler.allocation.file",
      "C:\\WorkSpaceIdea\\FirstMavenScala\\src\\main\\resources\\fairscheduler.xml")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //    conf.set("spark.kryo.registrator", "river.wang.com.study.exercise.MyRegistrator")

    val sc = new SparkContext(conf)

    //    runJobTest(sc)
    //    testStrSer(sc)
    //    testFastUtil(sc)
    testKryo(sc)

    Thread.sleep(1000 * 60 * 1000)
    sc.stop()
  }

  def runJobTest(sc: SparkContext): Unit = {
    sc.runJob(sc.parallelize(1 to 1, 2), (i: Iterator[Int]) => {
      println(s"====${i.size}=====")
      i.foreach(println)
    })
  }

  def testStrSer(sc: SparkContext): Unit = {
    val strings = "王晓茜"

    val byteArray = "王晓茜".getBytes("UTF-8")

    val cachedStrings = sc.parallelize(strings, 1).cache()

    val cachedBytes = sc.parallelize(byteArray, 1).cache()

    val cachedSerBytes = sc.parallelize(byteArray, 1).persist(StorageLevel.MEMORY_ONLY_SER)



    val strPool = ThreadUtils.newFixedThreadPool(3, "string-thread")
    strPool.submit(new Runnable {
      override def run(): Unit = {
        //        sc.setLocalProperty("spark.scheduler.pool", "production")
        cachedStrings.map(item => Thread.sleep(300 * 60 * 1000)).foreach(println)
      }
    })

    strPool.submit(new Runnable {
      override def run(): Unit = {
        //        sc.setLocalProperty("spark.scheduler.pool", "test")
        cachedBytes.map(item => Thread.sleep(300 * 60 * 1000)).foreach(println)
      }
    })

    strPool.submit(new Runnable {
      override def run(): Unit = {
        //        sc.setLocalProperty("spark.scheduler.pool", "test")
        cachedSerBytes.map(item => Thread.sleep(300 * 60 * 1000)).foreach(println)
      }
    })
  }

  def testFastUtil(sc: SparkContext): Unit = {
    val map = Map[Long, String](1L -> "王晓茜", 2L -> "王小东")
    val newMap = new Long2ReferenceOpenHashMap[Array[Byte]]()
    map.foreach(tuple => newMap.put(tuple._1, tuple._2.getBytes("UTF-8")))

    val cachedMap = sc.parallelize(Array(map)).cache()
    val cachedNewMap = sc.parallelize(Array(newMap)).cache()

    val mapPool = ThreadUtils.newFixedThreadPool(2, "map-thread")
    mapPool.submit(new Runnable {
      override def run(): Unit = {
        //        sc.setLocalProperty("spark.scheduler.pool", "production")
        cachedMap.map(item => Thread.sleep(300 * 60 * 1000)).count()
      }
    })

    mapPool.submit(new Runnable {
      override def run(): Unit = {
        //        sc.setLocalProperty("spark.scheduler.pool", "production")
        cachedNewMap.map(item => Thread.sleep(300 * 60 * 1000)).count()
      }
    })
  }

  def testKryo(sc: SparkContext) = {
    val testData1 = new Test1("王晓茜", 28)
    val testData2 = new Test2("王晓茜", 28)

    //    val value1 = sc.broadcast(testData1)
    //    val value2 = sc.broadcast(testData2)
    //
    //    sc.parallelize(1 to 2).map(item => {
    //      item + value1.value.name
    //    }).count()

    val kryoPool = ThreadUtils.newFixedThreadPool(2, "kryo-thread")
    kryoPool.submit(new Runnable {
      override def run(): Unit = {
        val value1 = sc.broadcast(testData1)
        println("==================================1===============================")
        //        sc.setLocalProperty("spark.scheduler.pool", "production")
        sc.parallelize(1 to 2).map(item => {
          item + value1.value.name
        }).count()
        println("=================================2================================")
      }
    })
    kryoPool.submit(new Runnable {
      override def run(): Unit = {
        val value2 = sc.broadcast(testData2)
        //        sc.setLocalProperty("spark.scheduler.pool", "production")
        println("==================================3===============================")
        sc.parallelize(1 to 2).map(item => {
          value2.value.name + item
        }).count()
        println("==================================4===============================")
      }
    })
  }
}

class Test1(val name: String, val age: Int)

class Test2(val name: String, val age: Int)

class MyRegistrator() extends KryoRegistrator {
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[Test1])
  }
}
