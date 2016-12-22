package river.wang.com.study.concurrency

import java.util.concurrent.{Executors, Semaphore}

/**
  * 按顺序执行，只有一个释放了，才能向下执行
  */
object SemaphoreTest {

  def main(args: Array[String]): Unit = {

    val semaphore = new Semaphore(0)

    val executor = Executors.newSingleThreadExecutor()

    executor.submit(new Runnable {
      override def run(): Unit = {
        println("========start thread1==========")
        Thread.sleep(3000)
        semaphore.release()
        println("========end thread1==========")
      }
    })
    println("========start semaphore.acquire()==========")
    semaphore.acquire()
    println("========end semaphore.acquire()==========")

  }

}
