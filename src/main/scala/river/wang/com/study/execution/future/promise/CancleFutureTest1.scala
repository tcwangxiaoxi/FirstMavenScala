package river.wang.com.study.execution.future.promise

import river.wang.com.study.execution.utils.FutureOps

import scala.concurrent.Future

/**
  * 通过or方法，对原有Future进行对比，依据快的则占用Promise对象，实现提前对原有计算结果的完善，
  * 如果or的Future操作是不耗时的话，就可以实现关闭的功能，
  * 如果or的Future操作是一个固定的时间的话，就可以实现超时的功能，
  * 当然对原有计算的回收一定要进行考虑，防止线程的溢出！！！！
  * 所以这个方法不好，应计量使用CancleFutureTest2的实现。
  *
  * Created by wxx on 2016/12/28.
  */
class CancleFutureTest1 extends FutureOps {
  private val f = new TimeOutFutureTest()
  val result = f.timeout(1000).map(_ => "timeout!") or Future {
    Thread.sleep(500)
    "work completed!"
  }
  result.foreach(println)

}

object CancleFutureTest1 extends App {

  new CancleFutureTest1()
  Thread.sleep(10000)

}
