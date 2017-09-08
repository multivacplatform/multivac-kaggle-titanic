object ParamGridParameters {
  case class ParamGridParameters(
                                  maxIterArray: Array[Int],
                                  numTreesArr: Array[Int],
                                  maxBinsArr: Array[Int],
                                  maxDepthArr: Array[Int],
                                  impurityArr: Array[String],
                                  numFolds: Int,
                                  featureSubsetStrategy: String
                                )

  def loadConfigs(): ParamGridParameters = {

    val paramGridVariables = ParamGridParameters(
      Array(20, 50), //maxIter
      Array(300), //numTrees
      Array(10, 15), //maxBins
      Array(15, 20), //maxDepth
      Array("entropy", "gini"), //impurity
      10, //numFolds
      "auto" //featureSubsetStrategy
    )
    paramGridVariables
  }


}
