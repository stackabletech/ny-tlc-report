package org.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, dayofweek}

/**
 * Hello world!
 *
 */
object App {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("ny-tlc-report")
      .getOrCreate()

    val inputPath = args(0);
    try {
      val inputDf = spark.read.options(Map("header" -> "true", "inferSchema" -> "true")).csv(inputPath);
      val report = inputDf.select(col("passenger_count"),
          col("trip_distance"),
          col("total_amount"),
          dayofweek(col("tpep_pickup_datetime")).alias("day_of_week"))
      .groupBy(col("day_of_week"))
      .agg(Map("passenger_count" -> "sum", "trip_distance" -> "avg", "total_amount" -> "avg"))
      .withColumnRenamed("avg(total_amount)", "avg_amount")
      .withColumnRenamed("avg(trip_distance)", "avg_trip_distance")
      .withColumnRenamed("sum(passenger_count)", "total_passengers")
      .orderBy("day_of_week")

      report.show();
    }
    finally {
      spark.close();
    }
  }
}
