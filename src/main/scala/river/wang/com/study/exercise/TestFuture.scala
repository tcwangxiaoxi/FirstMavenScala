package river.wang.com.study.exercise

import java.util.concurrent.{Callable, Executors}

/**
  * Created by wxx on 2016/12/10.
  */
object TestFuture {
  def main(args: Array[String]): Unit = {

//    val String = "王晓茜"

    val byteArray = "王晓茜".getBytes("UTF-8")
    println(new String(byteArray))



    val pool = Executors.newFixedThreadPool(5)

    val result1 = pool.submit(new Callable[Int] {
      override def call(): Int = {
        Thread.sleep(3000)
        1
      }
    })

    println(result1.isDone)
    println(result1.get())
  }

}
