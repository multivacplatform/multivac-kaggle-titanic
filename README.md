# Machine Learning from Disaster (Kaggle) 
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/multivacplatform/multivac-kaggle-titanic/blob/master/LICENSE) [![Build Status](https://travis-ci.org/multivacplatform/multivac-kaggle-titanic.svg?branch=master)](https://travis-ci.org/multivacplatform/multivac-kaggle-titanic) [![Multivac Discuss](https://img.shields.io/badge/multivac-discuss-ff69b4.svg)](https://discourse.iscpif.fr/c/multivac) [![Multivac Channel](https://img.shields.io/badge/multivac-chat-ff69b4.svg)](https://chat.iscpif.fr/channel/multivac)

This repo is just for learning purposes to anyone who is new to Machine Learning by Apache Spark.
https://www.kaggle.com/c/titanic

## Environment and Tests
* Scala 2.11.x
* Apache Spark 2.2
* Tests locally and in Cloudera (CDH 5.12)

## How-To
* sbt update
* sbt "run local" - This runs the code on your local machine
* sbt pacakge - to use the JAR by spark-submit 
* You can set ParamGrid values for cross validation inside ParamGridParameters.scala 

## Re-used Codes

* [Exploring spark.ml with the Titanic Kaggle competition](https://benfradet.github.io/blog/2015/12/16/Exploring-spark.ml-with-the-Titanic-Kaggle-competition)
* [Titanic: Machine Learning from Disaster (Kaggle)](https://databricks-prod-cloudfront.cloud.databricks.com/public/4027ec902e239c93eaaa8714f173bcfc/19095846306138/45566022600459/8071950455163429/latest.html)

## Code of Conduct

This, and all github.com/multivacplatform projects, are under the [Multivac Platform Open Source Code of Conduct](https://github.com/multivacplatform/code-of-conduct/blob/master/code-of-conduct.md). Additionally, see the [Typelevel Code of Conduct](http://typelevel.org/conduct) for specific examples of harassing behavior that are not tolerated.

## Useful Links

* [Building Classification model using Apache Spark](http://vishnuviswanath.com/spark_lr.html)
* [Revisit Titanic Data using Apache Spark](https://6chaoran.wordpress.com/2016/08/13/__trashed/)
* [Would You Survive the Titanic? A Guide to Machine Learning in Python](https://blog.socialcops.com/engineering/machine-learning-python/)

## Copyright and License

Code and documentation copyright (c) 2017 [ISCPIF - CNRS](http://iscpif.fr). Code released under the [MIT license](https://github.com/multivacplatform/multivac-kaggle-titanic/blob/master/LICENSE).
