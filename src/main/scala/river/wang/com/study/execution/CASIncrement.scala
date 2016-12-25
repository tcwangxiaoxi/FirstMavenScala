package river.wang.com.study.execution

import java.util.concurrent.atomic.AtomicLong

import scala.annotation.tailrec

/**
  * Created by wxx on 2016/12/25.
  */
object CASIncrement {

  private val num = new AtomicLong(0)


  /**
    * 递增数值的获取方法
    *
    * 关于tailrec注解：永远都应对有可能执行尾递归操作的函数使用tailrec注解！
    *         这会使编译器检查所有带有该注解的函数，查明它们是否执行尾递归操作。
    * @return
    */
  @tailrec
  def getIncrementNum: Long = {
    val oldNum = num.get
    val newNum = oldNum + 1
    // 通过CAS操作，递增数值
    if (num.compareAndSet(oldNum, newNum))
      newNum
    else getIncrementNum
  }
}
