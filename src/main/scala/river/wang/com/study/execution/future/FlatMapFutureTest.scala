package river.wang.com.study.execution.future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

/**
  * Created by wxx on 2016/12/26.
  */
object FlatMapFutureTest extends App {
  try {
    val netiquetteUrl = "http://www.ietf.org/rfc/rfc1855.txt"
    val netiquette = Future {
      Source.fromURL(netiquetteUrl).mkString
    }
    val urlSpecUrl = "http://www.3c.org/Addressing/URL/rul-spec.txt"
    val urlSpec = Future {
      Source.fromURL(urlSpecUrl).mkString
    }

    /**
      * flatMap 与 map 的区别在于参数的返回值，
      * flatMap可以接受返回Future的对象,把两个异步的方法连接起来，
      * 而map只能返回具体值类型的对象
      */
    val answer = netiquette.flatMap { nettext =>
      urlSpec.map { urltext =>
        s"Check this out: $nettext. And check out: $urltext"
      }
    }

    /**
      * 第二种方式也可以连接两个异步操作，
      * 但是使用for循环时，只有当第一个Future对象被完善后，
      * 完善第二个Future对象的操作才会被执行。
      * 当需要用过异步方式使用nettext值计算第二个Future对象的完善时，
      * 这种模式才具有意义
      */
    val answer2 = for {
      nettext <- netiquette
      urltext <- urlSpec
    } yield {
      s"First, read this: $nettext. Now, try this: $urltext"
    }

    answer2.foreach {
      content => println(content)
    }



    Thread.sleep(30 * 60 * 1000)

  } catch {
    case e: Throwable => println(e)
  }
}
