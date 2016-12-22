package river.wang.com.study.sparksql.hive

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import river.wang.com.study.MyUtils


object MyUDF extends MyUtils {
  def main(args: Array[String]): Unit = {
    val sc = sparkSQLContext("My UDAF App!")
    val rdd = sc.sparkContext.parallelize(Array("Spark", "Kafka", "Spark", "Kafka", "Hive", "Spark", "Spark")).map(Row(_))
    val schema = StructType(Array(StructField("word", StringType, nullable = true)))
    sc.createDataFrame(rdd, schema).registerTempTable("words")

    sc.udf.register("wordLength", (input: String) => input.length)
    sc.udf.register("wordCount", new MyUDF)

    sc.sql("select word,wordCount(word),wordLength(word) from words group by word").show()

    while ( true ) {}

  }
}

/**
  * 按照模板实现UDAF，对行数的统计
  * Created by wxx on 2016/11/1.
  */
class MyUDF extends UserDefinedAggregateFunction {

  /**
    * 该方法指定具体输入数据的类型
    *
    * @return
    */
  override def inputSchema: StructType = StructType(Array(StructField("input", StringType, nullable = true)))

  /**
    * 在进行聚合操作的时候所要处理的数据的结果的类型
    *
    * @return
    */
  override def bufferSchema: StructType = StructType(Array(StructField("count", IntegerType, nullable = true)))

  /**
    * 指定UDAF函数计算后返回的结果类型
    *
    * @return
    */
  override def dataType: DataType = IntegerType

  /**
    * 确保一致性（一般为true）
    *
    * @return
    */
  override def deterministic: Boolean = true

  /**
    * 在Aggregate之前，每组数据的初始化结果
    *
    * @param buffer
    */
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0
  }

  /**
    * 在进行聚合的时候，每当有新的值进来，对分组后的聚合如何进行计算
    * 本地的聚合操作，相当于Hadoop MapReduce 模型中的 Combiner
    *
    * @param buffer
    * @param input
    */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getAs[Int](0) + 1
  }

  /**
    * 最后在分布式节点进行Local Reduce 完成后，需要进行全局级别的 Merge 操作
    *
    * @param buffer1
    * @param buffer2
    */
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Int](0) + buffer2.getAs[Int](0)
  }

  /**
    * 返回 UDAF 最后的计算结果
    *
    * @param buffer
    * @return
    */
  override def evaluate(buffer: Row): Any = buffer.getAs[Int](0)
}


