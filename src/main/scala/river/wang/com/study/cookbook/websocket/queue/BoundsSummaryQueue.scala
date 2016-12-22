package river.wang.com.study.cookbook.websocket.queue

import java.util.concurrent.ArrayBlockingQueue

import scala.reflect.ClassTag

/**
  * Created by wxx on 2016/12/19.
  */
class BoundsSummaryQueue[B, A: ClassTag](maxSize: Int) {

  private var summary: B = _

  private val queue = new ArrayBlockingQueue[A](maxSize)

  def put(summary: B, elem: A): Unit = {
    if (queue.size() == maxSize) {
      queue.poll()
    }
    queue.put(elem)
    this.summary = summary
  }

  def getSummaryInfo: (B, Array[A]) = {
    val iterator = queue.iterator()
    val array = new Array[A](queue.size())
    var i = 0
    while (iterator.hasNext) {
      array(i) = iterator.next()
      i += 1
    }
    (summary, array)
  }

  def getSummaryInfo2: (B, Array[A]) = {
    val array = new Array[A](0)
    (summary, array)
  }

}
