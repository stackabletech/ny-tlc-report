package tech.stackable.demo.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, dayofweek}

import java.io.File
import scala.io.Source

case class CommandLineArgs(
                            input: File = new File("tlc.txt")
                          )

/**
 * Generate a report from the NY TLC data.
 *
 * The command line argument --input is expected to point to a plain text file containing a list of S3 paths.
 */
object NYTLCReport {
  val appName = "ny-tlc-report"

  def commandLineArgs(args: Array[String]): Option[CommandLineArgs] = {
    import scopt.OParser
    val builder = OParser.builder[CommandLineArgs]
    val parser = {
      import builder._
      OParser.sequence(
        programName(appName),
        opt[File]('i', "input")
          .required()
          .action((x, c) => c.copy(input = x))
          .text("Name of the input file containing paths to the data"),
      )
    }

    OParser.parse(parser, args, CommandLineArgs())
  }

  def main(args: Array[String]): Unit = {

    val input = commandLineArgs(args) match {
      case Some(cla) => Source.fromFile(cla.input).getLines().filter(line => line.nonEmpty).toArray
      case _ => Array()
    }
    val spark = SparkSession
      .builder
      .appName(appName)
      .getOrCreate()

    try {
      val inputDf = spark.read.options(Map("header" -> "true", "inferSchema" -> "true")).csv(input: _*)
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

      report.show()
    }
    finally {
      spark.close()
    }
  }
}
