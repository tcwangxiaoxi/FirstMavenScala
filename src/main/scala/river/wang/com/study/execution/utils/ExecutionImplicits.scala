package river.wang.com.study.execution.utils


/**
  * 可以通过scala的隐式转换功能，
  * 实现依赖注入的目的，注入默认的隐式参数
  *
  * Created by wxx on 2016/12/28.
  */
trait ExecutionImplicits {

  implicit val ectx = scala.concurrent.ExecutionContext.Implicits.global

}


