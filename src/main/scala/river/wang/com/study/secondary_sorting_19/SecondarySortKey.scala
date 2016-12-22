package river.wang.com.study.secondary_sorting_19

/**
  * 二次排序的具体步骤
  *
  * 1、按照Ordered和Serializable接口实现自定义排序的Key
  * 2、将要进行二次排序的文件加载进来生成<Key,Value>类型的RDD
  * 3、使用sortByKey基于自定义的Key进行二次排序
  * 4、去除掉排序的Key，只保留排序的结果
  *
  * Created by wxx on 2016/10/19.
  */
case class SecondarySortKey(first: Int, second: Int) extends Ordered[SecondarySortKey] with Serializable {
  override def compare(that: SecondarySortKey): Int = {
    if (this.first - that.first != 0) {
      this.first - that.first
    } else {
      this.second - that.second
    }
  }
}
