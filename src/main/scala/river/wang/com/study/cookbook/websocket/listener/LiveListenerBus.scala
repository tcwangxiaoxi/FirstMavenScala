package river.wang.com.study.cookbook.websocket.listener

import java.util.concurrent.atomic.AtomicBoolean

import org.apache.commons.logging.LogFactory

/**
  * Created by wxx on 2016/12/22.
  */
class LiveListenerBus
  extends AsynchronousListenerBus[MyListener, MyListenerEvent]("MyListenerBus")
    with MyListenerBus {

  private val log = LogFactory.getLog(LiveListenerBus.this.getClass)

  private val logDroppedEvent = new AtomicBoolean(false)

  override def onDropEvent(event: MyListenerEvent): Unit = {
    if (logDroppedEvent.compareAndSet(false, true)) {
      log.error("事件消息队列空间已满。有可能是因为其中一个消息处理的太慢，" +
        "导致无法有效的处理队列剩下的事件，导致堆积。")
    }
  }

}
