package river.wang.com.study.cookbook.websocket.listener

/**
  * Created by wxx on 2016/12/20.
  */
trait MyListenerBus extends AsynchronousListenerBus[MyListener, MyListenerEvent] {

  override def onPostEvent(listener: MyListener, event: MyListenerEvent): Unit = {
    event match {
      case e: MyListenerRecorderStartdGenerated => listener.onRecorderStartdGenerated()
      case e: MyListenerAllRecordersFinishGenerated => listener.onAllRecordersFinishGenerated()

    }
  }
}
