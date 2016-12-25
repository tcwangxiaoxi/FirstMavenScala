package river.wang.com.study.future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by wxx on 2016/12/24.
  */
object CallbackFutureTest {

  val urlSpec: Future[List[String]] = getUrlSpec

  def getUrlSpec: Future[List[String]] = Future {
    Thread.sleep(1000)
    println("getUrlSpec")
    List("1a", "2bc", "3ab")
  }

  def find(lines: List[String], keyword: String): String = {
    Thread.sleep(1000)
    println("find")
    lines.zipWithIndex.collect {
      case (line, n) if line.contains(keyword) =>
        (n, line)
    }.mkString("\n")
  }

  def main(args: Array[String]): Unit = {

    Thread.sleep(5000)

    urlSpec.foreach {
      lines =>
        Thread.sleep(60*60*1000)
        println("foreach")
        println("result: " + find(lines, "a"))
    }
    urlSpec.foreach {
      lines =>
        println("result2：" + find(lines, "b"))
    }

    println("回调函数已经注册，继续处理其他任务")
    Thread.sleep(60*60*1000)

  }


}


