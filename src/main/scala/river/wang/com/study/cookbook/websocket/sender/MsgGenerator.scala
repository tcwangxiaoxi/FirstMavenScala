package river.wang.com.study.cookbook.websocket.sender

/**
  * Created by wxx on 2016/12/19.
  */
trait MsgGenerator {
  def generateData(): String
}
