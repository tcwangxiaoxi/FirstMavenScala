package river.wang.com.study.execution.future

import java.io.File

import org.apache.commons.io.FileUtils

import scala.collection.convert.decorateAsScala._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/**
  * Created by wxx on 2016/12/26.
  */
object MapFutureTest extends App {

  def catchException[T](body: => T): Option[T] = {
    try {
      val result = body
      Some(result)
    } catch {
      case NonFatal(t) =>
        println(t)
        None
    }
  }

  def blacklistFile(name: String): Future[List[String]] = Future {
    catchException {
      val lines = Source.fromFile(name).getLines
      lines.filter(x => !x.startsWith("#") && !x.isEmpty).toList
    } match {
      case Some(s) => s
      case _ => null
    }
  }

  def findFiles(patterns: List[String]): List[String] = {
    val root = new File("test-datas")
    for {
      f <- FileUtils.iterateFiles(root, null, true).asScala.toList
      pat <- patterns
      abspat = root.getCanonicalPath + File.separator + pat
      if f.getCanonicalPath.equals(abspat)
    } yield f.getCanonicalPath

  }

  // 找到符合要求的文件列表，并输出
  blacklistFile("test-datas/filepath").foreach {
    patterns =>
      val files = findFiles(patterns)
      //      throw new NullPointerException("123")
      println(s"matches:\n${files.mkString("\n")}")
  }

  // 找到符合要求的文件列表，并通过异步的函数式的方式进行处理
  val reulst = blacklistFile("test-datas/filepath").map {
    patterns =>
      // 如果抛出了异常，没有被处理，则该计算不会抛出异常，就忽略了
      // 所以一定要记得对异常进行处理
      throw new NullPointerException("123")
      Success(findFiles(patterns))
  }
  // 处理异常
  val catchedResult = reulst.recover {
    case NonFatal(e) => Failure(e)
  }
  // 异步输出结果
  catchedResult.foreach {
    case Success(results) => println(s"matches:\n${results.mkString("\n")}")
    case Failure(error) => error.printStackTrace()
  }

  Thread.sleep(1000)
}
