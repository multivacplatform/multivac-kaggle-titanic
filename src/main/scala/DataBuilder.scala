import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object DataBuilder {



  def loadData(env: String,
               spark: SparkSession,
               trainFile: String,
               testFile: String
              ): (DataFrame, DataFrame) = {

    var storagePath = ConfigFactory.load().getString("spark.local.storagePath.value")
    if(env == "prod")
      storagePath = ConfigFactory.load().getString("spark.prod.master.value")

    val nullable = true
    val schemaArray = Array(
      StructField("PassengerId", IntegerType, nullable),
      StructField("Survived", IntegerType, nullable),
      StructField("Pclass", IntegerType, nullable),
      StructField("Name", StringType, nullable),
      StructField("Sex", StringType, nullable),
      StructField("Age", FloatType, nullable),
      StructField("SibSp", IntegerType, nullable),
      StructField("Parch", IntegerType, nullable),
      StructField("Ticket", StringType, nullable),
      StructField("Fare", FloatType, nullable),
      StructField("Cabin", StringType, nullable),
      StructField("Embarked", StringType, nullable)
    )

    val trainSchema = StructType(schemaArray)
    val testSchema = StructType(schemaArray.filter(p => p.name != "Survived"))

    val trainDF = spark.read
      .format("csv")
      .option("header", "true")
      .schema(trainSchema)
      .load(storagePath + trainFile)

    val testDF = spark.read
      .format("csv")
      .option("header", "true")
      .schema(testSchema)
      .load(storagePath + testFile)

    (trainDF, testDF)
  }
  def createExtraFeatures(trainDF: DataFrame, testDF: DataFrame): (DataFrame, DataFrame) = {
    val familySize: ((Int, Int) => Int) = (sibSp: Int, parCh: Int) => sibSp + parCh + 1
    val familySizeUDF = udf(familySize)
    val Pattern = ".*, (.*?)\\..*".r

    val titles = Map(
      "Capt"->       "Sir",
      "Col"->        "Sir",
      "Major"->      "Sir",
      "Don"->        "Sir",
      "Sir" ->       "Sir",
      "Rev"->        "Sir",
      "Jonkheer"->   "Lady",
      "the Countess"->"Lady",
      "Lady" ->      "Lady",
      "Dona"->       "Lady",
      "Dr"->         "Dr",
      "Mr" ->        "Mr",
      "Mme"->        "Mlle",
      "Mlle"->       "Mlle",
      "Ms"->         "Mrs",
      "Mrs" ->       "Mrs",
      "Miss" ->      "Miss",
      "Master" ->    "Master"
    )
    val title: ((String, String) => String) = {
      case (Pattern(t), sex) => titles.get(t) match {
        case Some(tt) => tt
        case None =>
          if (sex == "male") "Mr"
          else "Mrs"
      }
      case _ => "Mr"
    }
    val titleUDF = udf(title)

    val newTrainDF = trainDF
      .withColumn("FamilySize", familySizeUDF(col("SibSp"), col("Parch")))
      .withColumn("Title", titleUDF(col("Name"), col("Sex")))
      .withColumn("SurvivedString", trainDF("Survived").cast(StringType))
    val newTestDF = testDF
      .withColumn("FamilySize", familySizeUDF(col("SibSp"), col("Parch")))
      .withColumn("Title", titleUDF(col("Name"), col("Sex")))
      .withColumn("SurvivedString", lit("0").cast(StringType))

    (newTrainDF, newTestDF)
  }

  def fillNAValues(trainDF: DataFrame, testDF: DataFrame): (DataFrame, DataFrame) = {
    val avgAge = trainDF.select("Age").union(testDF.select("Age"))
      .agg(avg("Age"))
      .collect() match {
      case Array(Row(avg: Double)) => avg
      case _ => 0
    }

    val avgFare = trainDF.select("Fare").union(testDF.select("Fare"))
      .agg(avg("Fare"))
      .collect() match {
      case Array(Row(avg: Double)) => avg
      case _ => 0
    }

    val fillNAMap = Map(
      "Fare"     -> avgFare,
      "Age"      -> avgAge,
      "Embarked" -> "S"
    )

    val embarked: (String => String) = {
      case "" => "S"
      case a  => a
    }
    val embarkedUDF = udf(embarked)

    val newTrainDF = trainDF
      .na.fill(fillNAMap)
      .withColumn("Embarked", embarkedUDF(col("Embarked")))

    val newTestDF = testDF
      .na.fill(fillNAMap)
      .withColumn("Embarked", embarkedUDF(col("Embarked")))

    (newTrainDF, newTestDF)
  }
}