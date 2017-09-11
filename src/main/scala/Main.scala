
object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)

    val (dataDFRaw, predictDFRaw) = DataBuilder.loadData(env, spark, "train.csv", "test.csv")

    val (dataDFExtra, predictDFExtra) = DataBuilder.createExtraFeatures(dataDFRaw, predictDFRaw)

    val (dataDFCompleted, predictDFCompleted) = DataBuilder.fillNAValues(dataDFExtra, predictDFExtra)

    val (labelIndexer, assembler, labelConverter, stringIndexers, dataDFFiltered, predictDFFiltered, featColName) =
      FeatureBuilder.createVectors(
        dataDFCompleted,
        predictDFCompleted
      )

    val (cvModel, paramGrid) = PiplelineBuilder.fitSampleTrainingData(
      spark, env,
      labelIndexer, assembler, labelConverter, stringIndexers, featColName,
      dataDFFiltered, predictDFFiltered
    )
    Evaluator.displayAllModels(cvModel, paramGrid)
    SaveOutput.saveResult(cvModel, predictDFFiltered, env)

  }
}
