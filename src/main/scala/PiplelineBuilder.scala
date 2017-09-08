import org.apache.spark.ml.classification.{DecisionTreeClassifier, GBTClassificationModel, GBTClassifier, RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator, RegressionEvaluator}
import org.apache.spark.ml.feature.{IndexToString, StringIndexerModel, VectorAssembler}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.DataFrame


object PiplelineBuilder {
  val splitSeed = 12345

  def definePipeline(
                      idxdLabelColName: String,
                      featColName: String,
                      labelIndexer: StringIndexerModel,
                      assembler: VectorAssembler,
                      labelConverter: IndexToString,
                      stringIndexers: Seq[StringIndexerModel],
                      loadedConfigs: ParamGridParameters.ParamGridParameters
                    ): (Pipeline, Array[ParamMap], CrossValidator) ={


    val evaluatorBinary = new BinaryClassificationEvaluator()
      .setLabelCol("SurvivedIndexed")
      .setMetricName("areaUnderROC")

    val evaluatorMulticlass = new MulticlassClassificationEvaluator()
      .setLabelCol("SurvivedIndexed")
      .setMetricName("accuracy")

    val randomForest = new RandomForestClassifier()
      .setLabelCol(idxdLabelColName)
      .setFeaturesCol(featColName)
      .setFeatureSubsetStrategy(loadedConfigs.featureSubsetStrategy)
      .setSeed(splitSeed)

    val pipelineForest = new Pipeline()
      .setStages((stringIndexers :+ labelIndexer :+ assembler :+ randomForest :+ labelConverter).toArray)

    val paramGridForest = new ParamGridBuilder()
      .addGrid(randomForest.numTrees, loadedConfigs.numTreesArr)
      .addGrid(randomForest.maxBins, loadedConfigs.maxBinsArr)
      .addGrid(randomForest.maxDepth,loadedConfigs. maxDepthArr)
      .addGrid(randomForest.impurity, loadedConfigs.impurityArr)
      .build()

    val cvForest = new CrossValidator()
      .setEstimator(pipelineForest)
      .setEvaluator(evaluatorMulticlass)
      .setEstimatorParamMaps(paramGridForest)
      .setNumFolds(loadedConfigs.numFolds).setSeed(splitSeed)

    (pipelineForest, paramGridForest, cvForest)
  }

  def fitCVModel(
                  cvForest: CrossValidator,
                  paramGridForest: Array[ParamMap],
                  dataDFFiltered: DataFrame
                ): Unit ={
    val Array(training, test) = dataDFFiltered.randomSplit(Array(0.7, 0.3), splitSeed)
    training.cache
    test.cache

    val startTime = System.nanoTime()
    val crossValidatorModelForest = cvForest.fit(training)
    val elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished CrossValidating RandomForestClassifier. Summary:")
    println(s"Training time (sec)\t$elapsed")

    val avgMetricsParamGrid = crossValidatorModelForest.avgMetrics
    val combined = paramGridForest.zip(avgMetricsParamGrid)
    val cvBestModel = crossValidatorModelForest.bestModel.asInstanceOf[PipelineModel]
    combined.sortBy(_._2).foreach(println)
    // Explain params for each stage
  }

}
