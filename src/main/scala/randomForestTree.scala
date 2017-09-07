import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object randomForestTree {
  def start(spark: SparkSession, env: String): Unit ={

    var storagePath = ConfigFactory.load().getString("spark.local.storagePath.value")
    if(env == "prod")
      storagePath = ConfigFactory.load().getString("spark.prod.master.value")

    val (dataDFRaw, predictDFRaw) = dataBuilder.loadData(
      spark,
      storagePath + "train.csv",
      storagePath + "test.csv"
    )

    dataDFRaw.show()
    predictDFRaw.show()
  }
}
