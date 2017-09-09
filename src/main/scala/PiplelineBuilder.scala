import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.classification.{DecisionTreeClassifier, GBTClassificationModel, GBTClassifier, RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator, RegressionEvaluator}
import org.apache.spark.ml.feature.{IndexToString, StringIndexerModel, VectorAssembler}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.{DataFrame, SparkSession}


object PiplelineBuilder {
  val splitSeed = 12345

  def definePipeline(
                      spark: SparkSession,
                      env: String
                    ): Unit = {

    var storagePath = ConfigFactory.load().getString("spark.local.storagePath.value")
    if(env == "prod")
      storagePath = ConfigFactory.load().getString("spark.prod.master.value")

    val (dataDFRaw, predictDFRaw) = DataBuilder.loadData(spark, storagePath + "train.csv", storagePath + "test.csv")

    val (dataDFExtra, predictDFExtra) = DataBuilder.createExtraFeatures(dataDFRaw, predictDFRaw)
    val (dataDFCompleted, predictDFCompleted) = DataBuilder.fillNAValues(dataDFExtra, predictDFExtra)

    val numericFeatColNames = Seq("Age", "SibSp", "Parch", "Fare", "FamilySize")
    val categoricalFeatColNames = Seq("Pclass", "Sex", "Embarked", "Title")

    val labelColName = "SurvivedString"
    val featColName = "Features"
    val idColName = "PassengerId"

    val idxdLabelColName = "SurvivedIndexed"

    val (labelIndexer, assembler, labelConverter, stringIndexers, dataDFFiltered, predictDFFiltered) = FeatureBuilder.createVectors(
      numericFeatColNames,
      categoricalFeatColNames,
      labelColName,
      featColName,
      idColName,
      idxdLabelColName,
      dataDFCompleted,
      predictDFCompleted
    )

    val loadedConfigs = ParamGridParameters.loadConfigs()
    //    val evaluatorBinary = new BinaryClassificationEvaluator()
    //      .setLabelCol("SurvivedIndexed")
    //      .setMetricName("areaUnderROC")

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

    val Array(training, test) = dataDFFiltered.randomSplit(Array(0.8, 0.2), splitSeed)
    training.cache
    test.cache

    val startTime = System.nanoTime()
    val crossValidatorModelForest = cvForest.fit(training)
    val elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished CrossValidating RandomForestClassifier. Summary:")
    println(s"Training time (sec)\t$elapsed")

    val avgMetricsParamGrid = crossValidatorModelForest.avgMetrics
    val combined = paramGridForest.zip(avgMetricsParamGrid)
    combined.sortBy(_._2).foreach(println)
    // Explain params for each stage

  }
}
