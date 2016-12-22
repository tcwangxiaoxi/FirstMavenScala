package river.wang.com.study.cookbook.websocket.listener

/**
  * Created by wxx on 2016/12/22.
  */
trait MyListener {

  def onRecorderStartdGenerated() {}

  def onAllRecordersFinishGenerated() {}

}

sealed trait MyListenerEvent

case class MyListenerRecorderStartdGenerated() extends MyListenerEvent

case class MyListenerAllRecordersFinishGenerated() extends MyListenerEvent
