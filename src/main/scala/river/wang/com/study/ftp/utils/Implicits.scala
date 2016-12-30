package river.wang.com.study.ftp.utils

import scala.collection.convert.DecorateAsScala

/**
  * Created by wxx on 2016/12/30.
  */
trait Implicits extends DecorateAsScala {
  implicit val ectx = scala.concurrent.ExecutionContext.Implicits.global
}
