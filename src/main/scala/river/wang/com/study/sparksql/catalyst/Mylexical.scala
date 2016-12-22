package river.wang.com.study.sparksql.catalyst

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by wxx on 2016/11/29.
  */
class Mylexical extends StandardTokenParsers with PackratParsers {
  //定义分割符
  lexical.delimiters ++= List(".", ";", "+", "-", "*")
  //定义表达式，支持加，减，乘
  lazy val expr: PackratParser[Int] = plus | minus | multi
  //加法表示式的实现
  lazy val plus: PackratParser[Int] = num ~ "+" ~ num ^^ { case n1 ~ "+" ~ n2 => n1.toInt + n2.toInt }
  //减法表达式的实现
  lazy val minus: PackratParser[Int] = num ~ "-" ~ num ^^ { case n1 ~ "-" ~ n2 => n1.toInt - n2.toInt }
  //乘法表达式的实现
  lazy val multi: PackratParser[Int] = num ~ "*" ~ num ^^ { case n1 ~ "*" ~ n2 => n1.toInt * n2.toInt }
  lazy val num = numericLit

  /**
    *
    * @param input
    * @return
    */
  def parse(input: String) = {
    //定义词法读入器myread，并将扫描头放置在input的首位
    val myread = new PackratReader(new lexical.Scanner(input))
    print("处理表达式 " + input)
    phrase(expr)(myread) match {
      case Success(result, _) => println(" Success!"); println(result); Some(result)
      case n => println(n); println("Err!"); None
    }
  }

}

object Mylexical {
  def main(args: Array[String]): Unit = {
    val mylexical = new Mylexical()
    val prg = "6 * 3" :: "24-/*aaa*/4" :: "a+5" :: "21/3" :: Nil
    prg.map(mylexical.parse)
  }
}
