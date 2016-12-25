package river.wang.com.study.future

import scala.util.{Failure, Success, Try}

/**
  * Created by wxx on 2016/12/25.
  */
object TryFutureTest extends App {

  val threadName: Try[String] = Try(Thread.currentThread().getName)
  val someText: Try[String] = Try("ba la.. ba la.. ba la...")
  val exception: Failure[String] = Failure(new NullPointerException("参数错误"))

  val msg: Try[String] = for {
    tn <- threadName
    st <- someText
  } yield s"Message $st was created on t = $tn"

  val msg2: Try[String] = for {
    tn <- threadName
    ex <- exception
  } yield s"Message $ex was created on t = $tn"

  def handleMessage(t: Try[String]) = t match {
    case Failure(error) if error.isInstanceOf[NullPointerException] => println(s"NullPointerException failure - $error")
    case Failure(error) => println(s"unexpected failure - $error")
    case Success(message) => println(message)
  }

  handleMessage(msg)
  handleMessage(msg2)
  msg.foreach(println)
  msg2.foreach(println)

}
