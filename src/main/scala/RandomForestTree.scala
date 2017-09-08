import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object RandomForestTree {
  def trainData(spark: SparkSession, env: String): Unit ={

    var storagePath = ConfigFactory.load().getString("spark.local.storagePath.value")
    if(env == "prod")
      storagePath = ConfigFactory.load().getString("spark.prod.master.value")

    val (dataDFRaw, predictDFRaw) = DataBuilder.loadData(
      spark,
      storagePath + "train.csv",
      storagePath + "test.csv"
    )

    /*
    dataDFRaw.show()
    predictDFRaw.show()
    */

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
    //
    val (myPipeline, myParamGrid, myCVModel) = PiplelineBuilder.definePipeline(
      idxdLabelColName,
      featColName,
      labelIndexer,
      assembler,
      labelConverter,
      stringIndexers,
      loadedConfigs
    )
    PiplelineBuilder.fitCVModel(myCVModel, myParamGrid, dataDFFiltered)
  }
}
