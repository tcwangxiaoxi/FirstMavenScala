package river.wang.com

/**
  * Created by wxx on 2016/10/18.
  */
object TestWithScope {

  def withScope(arg: String)(fun: => String): String = {
    println(arg)
    fun
  }

  def withScope2(fun: => String) = withScope("Step2")(fun)

  def delete(name: String) = withScope2 {
    println("deleteAction" + name)
    "deleteA"
  }


  def main(args: Array[String]) {

    println(delete("Step1"))
  }


}
