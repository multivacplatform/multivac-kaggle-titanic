name := "multivac-kaggle-titanic"

version := "0.5"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers ++= Seq(
  "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
  "Spray Repository" at "http://repo.spray.cc/",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Twitter4J Repository" at "http://twitter4j.org/maven2/",
  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
  "Twitter Maven Repo" at "http://maven.twttr.com/",
  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
  "Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  Resolver.sonatypeRepo("public")
)

libraryDependencies ++= Seq(
  "org.apache.spark" %%"spark-core" % "2.2.0",
  "org.apache.spark" %% "spark-sql" % "2.2.0",
  "org.apache.spark" %% "spark-streaming" % "2.2.0",
  "org.apache.spark" %% "spark-mllib" % "2.2.0",
  "org.apache.spark" %% "spark-hive" % "2.2.0",
  "org.apache.spark" %% "spark-graphx" % "2.2.0",
  "org.apache.spark" %% "spark-yarn" % "2.2.0",
  "com.typesafe" % "config" % "1.3.1"
)