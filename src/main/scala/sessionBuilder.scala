import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object sessionBuilder {
  def buildSession(env: String): SparkSession = {

    var sparkMaster = ConfigFactory.load().getString("spark.local.master.value")
    if(env == "prod")
      sparkMaster = ConfigFactory.load().getString("spark.prod.master.value")

    val spark: SparkSession = SparkSession.builder
      .appName("Kaggle - Titanic")
      .master(sparkMaster)
      .enableHiveSupport()
      .getOrCreate

    spark
  }
}
