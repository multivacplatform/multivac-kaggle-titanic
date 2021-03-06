import org.apache.spark.ml.feature._
import org.apache.spark.sql.DataFrame

object FeatureBuilder {

  def createVectors(
                      dataDFCompleted: DataFrame,
                      predictDFCompleted: DataFrame
                    ): (StringIndexerModel, VectorAssembler, IndexToString, Seq[StringIndexerModel], DataFrame, DataFrame, String) = {


    val numericFeatColNames = Seq("Age", "SibSp", "Parch", "Fare", "FamilySize")
    val categoricalFeatColNames = Seq("Pclass", "Sex", "Embarked", "Title")

    val labelColName = "SurvivedString"
    val featColName = "Features"
    val idColName = "PassengerId"

    val idxdLabelColName = "SurvivedIndexed"


    val idxdCategoricalFeatColName = categoricalFeatColNames.map(_ + "Indexed")
    val allFeatColNames = numericFeatColNames ++ categoricalFeatColNames
    val allIdxdFeatColNames = numericFeatColNames ++ idxdCategoricalFeatColName

    val allPredictColNames = allFeatColNames ++ Seq(idColName)

    val dataDFFiltered = dataDFCompleted.select(labelColName, allPredictColNames: _*)
    val predictDFFiltered = predictDFCompleted.select(labelColName, allPredictColNames: _*)

    val allData = dataDFFiltered.union(predictDFFiltered)
    allData.cache()

    val stringIndexers = categoricalFeatColNames.map { colName =>
      new StringIndexer()
        .setInputCol(colName)
        .setOutputCol(colName + "Indexed")
        .fit(allData)
    }

    val labelIndexer = new StringIndexer().setInputCol(labelColName).setOutputCol(idxdLabelColName).fit(allData)

    // vector assembler
    val assembler = new VectorAssembler().setInputCols(Array(allIdxdFeatColNames: _*)).setOutputCol(featColName)
    val labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)

    (labelIndexer, assembler, labelConverter, stringIndexers, dataDFFiltered, predictDFFiltered, featColName)
  }
}
