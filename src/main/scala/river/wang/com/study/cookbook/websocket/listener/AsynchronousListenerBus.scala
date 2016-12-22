package river.wang.com.study.cookbook.websocket.listener

import java.util.concurrent.{LinkedBlockingQueue, Semaphore}
import java.util.concurrent.atomic.AtomicBoolean

import org.apache.commons.logging.LogFactory
import river.wang.com.study.common.Interruptable
import river.wang.com.study.utils.ServiceUtils

import scala.util.DynamicVariable

/**
  * Created by wxx on 2016/12/20.
  */
abstract class AsynchronousListenerBus[L <: AnyRef, E](name: String) extends ListenerBus[L, E] {
  self =>

  private val log = LogFactory.getLog(AsynchronousListenerBus.getClass)
  private val EVENT_QUEUE_CAPACITY = 10000
  private val eventQueue = new LinkedBlockingQueue[E](EVENT_QUEUE_CAPACITY)

  // 用于表示是否启动了服务，被start方法调用改变状态
  private val started = new AtomicBoolean(false)
  // 用于表示是否停止了服务，被stop方法调用改变状态
  private val stopped = new AtomicBoolean(false)

  // 表示正在处理事件，通过self对象进行同步
  private var processingEvent = false

  // 一个任务计数器，根据队列中的任务数量控制服务处理事件，当没有任务时，挂起线程。
  private val eventLock = new Semaphore(0)

  private var service: Interruptable = null

  private val listenerThread = new Thread(name) {
    setDaemon(true)

    // tryOrStopSparkContext 当程序出现异常时，会级联的停止外部服务
    override def run(): Unit = ServiceUtils.tryOrStopSparkContext(service) {
      // 用于保证在停止服务之前，先必须要停止时间监听器
      AsynchronousListenerBus.withinListenerThread.withValue(true) {
        while (true) {
          // 首先，获取任务锁，没有任务就会挂起
          eventLock.acquire()
          self.synchronized {
            processingEvent = true
          }
          try {
            val envent = eventQueue.poll
            if (envent == null) {
              // 如果事件为空，表示出现问题，停止循环，
              // 并通过抛出异常关闭外部服务
              if (!stopped.get) {
                throw new IllegalStateException("Polling `null` from eventQueue means" +
                  " the listener bus has been stopped. So `stopped` must be true")
              }
              return
            }
            postToAll(envent)
          } finally {
            self.synchronized {
              processingEvent = false
            }
          }
        }
      }
    }
  }

  def start(service: Interruptable): Unit = {
    if (started.compareAndSet(false, true)) {
      this.service = service
      listenerThread.start()
    } else {
      throw new IllegalStateException("""$name already started!""")
    }
  }

  def post(event: E): Unit = {
    if (started.get) {
      log.error(s"$name has already stopped! Dropping event $event")
      return
    }
    val eventAdded = eventQueue.offer(event)
    if (eventAdded) {
      eventLock.release()
    } else {
      onDropEvent(event)
    }
  }

  def onDropEvent(event: E): Unit

}

object AsynchronousListenerBus {
  /**
    * 用于判断事件监听器服务是否已经停止，用于保证在停止服务之前，先必须要停止时间监听器
    */
  val withinListenerThread: DynamicVariable[Boolean] = new DynamicVariable[Boolean](false)
}

