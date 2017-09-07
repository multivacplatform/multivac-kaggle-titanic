import com.typesafe.config.ConfigFactory

object main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = sessionBuilder.buildSession(env)
    randomForestTree.start(spark, env)

  }
}
