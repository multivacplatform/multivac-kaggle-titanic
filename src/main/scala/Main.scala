import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)
    RandomForestTree.trainData(spark, env)

  }
}
