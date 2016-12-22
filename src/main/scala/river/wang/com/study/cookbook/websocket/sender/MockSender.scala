package river.wang.com.study.cookbook.websocket.sender

/**
  * Created by wxx on 2016/12/19.
  */
trait MockSender extends Runnable {

  val generator: MsgGenerator

}
