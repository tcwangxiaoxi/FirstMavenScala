package river.wang.com.study.execution.forkjoin

import java.util.concurrent._

/**
  * 通过ForkJoin框架，计算1+2+3+4的结果
  *
  * 来源：http://www.infoq.com/cn/articles/fork-join-introduction
  *
  * Created by wxx on 2016/12/25.
  */
object ForkJoinTest extends App {
  val pool = new ForkJoinPool()
  // 生成一个计算任务，负责计算 1+2+3..+10
  val countTask = new CountTask(1, 10)
  // 执行一个任务
  val result = pool.submit(countTask)
  try {
    println(result.get(1, TimeUnit.SECONDS))
  } catch {
    case e@(_: InterruptedException | _: ExecutionException | _: TimeoutException) =>
      println("e@(_: InterruptedException | _: ExecutionException | _: TimeoutException) - " + e.getMessage)
  }
  //  ForkJoinTask在执行的时候可能会抛出异常，但是我们没办法在主线程里直接捕获异常，
  //  所以ForkJoinTask提供了isCompletedAbnormally()方法来检查任务是否已经抛出异常或已经被取消了，
  //  并且可以通过ForkJoinTask的getException方法获取异常。使用如下代码：
  if (countTask.isCompletedAbnormally) {
    println("countTask.isCompletedAbnormally - " + countTask.getException)
  }

}

class CountTask(val start: Int, val end: Int) extends RecursiveTask[Int] {

  override def compute(): Int = {
    var sum = 0

    //如果任务足够小就计算任务
    val canCompute = (end - start) <= CountTask.THRESHOLD
    if (canCompute) {
      for (i <- start to end) sum += i
      //      throw new NullPointerException(s"空指针 $start to $end")
    } else {
      // 如果任务大于阀值，就分裂成两个子任务计算
      val middle = (start + end) / 2
      val leftTask = new CountTask(start, middle)
      val rightTask = new CountTask(middle + 1, end)
      // 执行子任务
      leftTask.fork()
      rightTask.fork()
      // 等待子任务执行完，并得到其结果
      val leftResult = leftTask.join()
      val rightResult = rightTask.join()
      // 合并子任务
      sum = leftResult + rightResult
    }
    sum
  }
}

object CountTask {
  val THRESHOLD = 2 //阀值
}
