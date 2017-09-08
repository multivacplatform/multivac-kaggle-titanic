object ParamGridBuilder {
  case class ParamGridParameters(
                                  maxIterArray: Array[Int],
                                  numTreesArr: Array[Int],
                                  maxBinsArr: Array[Int],
                                  maxDepthArr: Array[Int],
                                  impurityArr: Array[String],
                                  numFolds: Int,
                                  featureSubsetStrategy: String
                                )

  def loadConfigs(): ParamGridParameters ={

    val paramGridVariables = ParamGridParameters(
      Array(20, 50),
      Array(100, 150),
      Array(10, 15),
      Array(15, 20),
      Array("entropy", "gini"),
      10,
      "auto"
    )
    paramGridVariables
  }


}
