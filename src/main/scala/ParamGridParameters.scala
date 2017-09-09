import com.typesafe.config.ConfigFactory
import collection.JavaConverters._

object ParamGridParameters {
  case class ParamGridParameters(
                                  maxIterArray: Array[Int],
                                  numTreesArr: Array[Int],
                                  maxBinsArr: Array[Int],
                                  maxDepthArr: Array[Int],
                                  impurityArr: Array[String],
                                  numFolds: Int,
                                  featureSubsetStrategy: String,
                                  randomSplit: Array[Double]
                                )

  def loadConfigs(): ParamGridParameters = {
    val config = ConfigFactory.load()
    val paramGridVariables = ParamGridParameters(
      config.getIntList("paramGrid.maxIter").asScala.toArray.map{ x => x.toInt },
      config.getIntList("paramGrid.numTrees").asScala.toArray.map{ x => x.toInt },
      config.getIntList("paramGrid.maxBins").asScala.toArray.map{ x => x.toInt },
      config.getIntList("paramGrid.maxDepth").asScala.toArray.map{ x => x.toInt },
      config.getStringList("paramGrid.impurity").asScala.toArray,
      config.getInt("paramGrid.numFolds"),
      config.getString("paramGrid.featureSubsetStrategy"),
      config.getDoubleList("randomSplit").asScala.toArray.map{ x => x.toDouble }
    )
    paramGridVariables
  }
}
