package river.wang.com.study.cookbook

import java.util.Comparator

import it.unimi.dsi.fastutil.BigList

import scala.reflect.ClassTag

/**
  * Created by wxx on 2016/12/12.
  */
object SortSample {

  def main(args: Array[String]): Unit = {

    val nums = Array(1, 54, 6, 3, 78, 34, 12, 45)
    val intComparator = new Comparator[Int] {
      override def compare(o1: Int, o2: Int): Int = o1 - o2
    }


    bubbleSort(nums, intComparator)
    //    quickSort(nums,intComparator)

    println(nums.mkString(","))

  }

  def bubbleSort[T: ClassTag](items: Array[T], comparator: Comparator[T]): Array[T] = {
    val size = items.length
    for (i <- 0 until size) {
      for (j <- 0 until size - i - 1) {
        if (comparator.compare(items(j), items(j + 1)) > 0) {
          val temp = items(j)
          items(j) = items(j + 1)
          items(j + 1) = temp
        }
      }
    }
    items
  }

  def quickSort[T](nums: Array[T], comparator: Comparator[T]): Array[T] = {
    def sortAndChangeByRange(splitItem: T, low: Int, high: Int): Int = {
      var lowIndex = low
      var highIndex = high
      // 遍历直到高低重合
      while (lowIndex < highIndex) {
        // 从高端过滤，找一个比自己小的节点
        while (lowIndex < highIndex && comparator.compare(splitItem, nums(highIndex)) <= 0) {
          highIndex -= 1
        }
        // 找到后移动到低端
        nums(lowIndex) = nums(highIndex)
        // 从低端过滤，找一个比自己大的节点
        while (lowIndex < highIndex && comparator.compare(splitItem, nums(lowIndex)) >= 0) {
          lowIndex += 1
        }
        // 找到后移动到高端
        nums(highIndex) = nums(lowIndex)
      }
      // 把切分的值放在最后重合的位置上
      nums(lowIndex) = splitItem
      // 返回切分出的位置
      lowIndex
    }

    def quickSort(low: Int, high: Int) {
      if (low < high) {
        val middle = sortAndChangeByRange(nums(low), low, high)
        quickSort(low, middle - 1)
        quickSort(middle + 1, high)
      }
    }

    quickSort(0, nums.length - 1)
    nums
  }

  def quickSort[T <: Comparable[T]](nums: Array[T]): Unit = {

    quickSort(nums, new Comparator[T] {
      override def compare(o1: T, o2: T): Int = o1.compareTo(o2)
    })

  }


}
