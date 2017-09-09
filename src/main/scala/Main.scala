object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)
    PiplelineBuilder.definePipeline(spark, env)

  }
}
