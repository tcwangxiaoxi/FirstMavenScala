package river.wang.com.study.execution.future.promise

import river.wang.com.study.execution.utils.FutureOps

import scala.concurrent.Promise

/**
  * Promise 对象和 Future 对象代表了单一赋值变量的两个方面：
  * 使用Promise对象可以为Future对象赋值，
  * 而使用Future对象会使你编写的程序能够读取到该值。
  *
  * Created by wxx on 2016/12/28.
  */
object PromisesCreateTest extends App with FutureOps {

  val p = Promise[String]
  val q = Promise[String]

  p.future.foreach(x => {
    Thread.sleep(1000)
    println(s"p succeeded with '$x'")
  })
  Thread.sleep(1000)
  p.success("assigned")
  q failure new Exception("not kept")

  /**
    * 不应将值或异常赋予已经完善的Promise对象，
    * 这会导致程序抛出异常，如下面的代码
    */
  try {
    q success "重复赋值会导致异常"
  } catch {
    case e: RuntimeException => e.printStackTrace()
  }
  q.future.failed.foreach {
    t => {
      Thread.sleep(1000)
      println(s"q failed with $t")
    }
  }
  Thread.sleep(10000)
  p.future.or(q.future).foreach(println)

}
