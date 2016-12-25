package river.wang.com.study.execution.filelock

import java.util.concurrent.atomic.AtomicReference

/**
  * Created by wxx on 2016/12/25.
  */
class Entry(val isDir: Boolean) {
  val state = new AtomicReference[State](new Idle)

  private def prepareForDelete: Boolean = {
    state.get match {
      case i: Idle =>
        if (state.compareAndSet(state.get, new Deleting)) true
        else prepareForDelete
      case c: Creating =>
        println("File currently created, cannot delete.")
        false
      case c: Copying =>
        println("File currently copied, cannot delete.")
        false
      case d: Deleting =>
        false
    }
  }
}

sealed trait State

class Idle extends State

class Creating extends State

class Copying(val n: Int) extends State

class Deleting extends State

object Entry extends App {

  private val executor = scala.concurrent.ExecutionContext.Implicits.global

  private val entry = new Entry(false)

  for (i <- 0 to 10)
    executor.execute(new Runnable {
      override def run(): Unit = {
        println(s"$i 开始删除...")
        if (entry.prepareForDelete)
          println(s"$i 已删除...")
        else {
          println(s"$i 由于已经被删除，无法删除")
        }
      }
    })

  Thread.sleep(100 * 60 * 1000)
}