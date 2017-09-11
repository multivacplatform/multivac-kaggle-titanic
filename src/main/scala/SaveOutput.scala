import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

object SaveOutput {

  def saveResult(
                  crossValidatorModelForest: CrossValidatorModel,
                  predictDFFiltered: DataFrame,
                  env: String
                ): Unit ={

    var storagePath = ConfigFactory.load().getString("spark.local.storagePath.value")
    if(env == "prod")
      storagePath = ConfigFactory.load().getString("spark.prod.master.value")

    val savedPredictionsResult = crossValidatorModelForest.bestModel.transform(predictDFFiltered)

    savedPredictionsResult
      .withColumn("Survived", col("predictedLabel"))
      .select("PassengerId", "Survived")
      .coalesce(1).write.format("csv")
      .option("header", "true")
      .mode(SaveMode.Overwrite)
      .save(storagePath + "gender_submission.csv")

  }
}
