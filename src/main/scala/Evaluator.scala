import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Evaluator {

  def displayAllModels(
                        crossValidatorModelForest: CrossValidatorModel,
                        paramGridForest: Array[ParamMap]
                      ): Unit ={

    val avgMetricsParamGrid = crossValidatorModelForest.avgMetrics
    val combined = paramGridForest.zip(avgMetricsParamGrid)
    combined.sortBy(_._2).foreach(println)
    // Explain params for each stage
  }
  def displayEvaluations(): Unit ={

  }

  def displayRationCorrectness(
                                crossValidatorModelForest: CrossValidatorModel,
                                test: DataFrame,
                                spark: SparkSession
                              ): Unit ={
    import spark.implicits._

    val testForest = crossValidatorModelForest.bestModel.transform(test).select( "SurvivedIndexed", "prediction")
    val counttotal = testForest.count()
    val correct = testForest.filter($"SurvivedIndexed" === $"prediction").count()
    val wrong = testForest.filter(not($"SurvivedIndexed" === $"prediction")).count()
    testForest.filter($"prediction" === 0.0).filter($"SurvivedIndexed" === $"prediction").count()
    testForest.filter($"prediction" === 0.0).filter(not($"SurvivedIndexed" === $"prediction")).count()
    testForest.filter($"prediction" === 1.0).filter(not($"SurvivedIndexed" === $"prediction")).count()
    println(wrong.toDouble/counttotal.toDouble) // ratioWrong
    println(correct.toDouble/counttotal.toDouble) // ratioCorrect
  }
}
