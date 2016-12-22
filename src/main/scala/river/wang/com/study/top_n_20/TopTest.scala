package river.wang.com.study.top_n_20

import java.util

import river.wang.com.study.MyUtils

/**
  * Created by wxx on 2016/11/28.
  */
object TopTest extends MyUtils {

  def main(args: Array[String]): Unit = {

    val list = test3()

    for (i <- 0 until list.size()) {
      println(list.get(i))
    }

  }

  def test1(): util.List[People] = {
    val list = topN(People("w", 9), 5, new util.ArrayList[People])
    for (i <- 1 to 10) {
      topN(People("" + i, i), 5, list)
    }
    list
  }

  def test2(): util.List[People] = {
    val list = topN(People("w", 9), 5, new util.ArrayList[People])
    for (i <- 1 to 10) {
      topN(People("" + i, i), 5, list)
    }
    for (i <- 1 to 10) {
      topN(People("_" + i, i), 5, list)
    }
    list
  }

  def test3(): util.List[People] = {
    val list = topN(People("w", 9), 5, new util.ArrayList[People])
    for (i <- 9 to 11) {
      topN(People("" + i, i), 5, list)
    }
    list
  }

}

case class People(name: String, age: Int) extends Comparable[People] {
  override def compareTo(o: People): Int = {
    if (o == null)
      return 1
    this.age - o.age
  }
}
