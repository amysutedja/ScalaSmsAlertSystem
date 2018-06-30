name := "SmsAlertSystemV2"
version := "1.0"
scalaVersion := "2.12.2"

lazy val `smsalertsystemv2` = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= Seq(
  ehcache,
  ws,
  guice,
  "com.twilio.sdk" % "twilio" % "7.15.0",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.11.351",
  "com.typesafe.play" %% "play-slick" %  "3.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2",
  "com.h2database" % "h2" % "1.4.194",
  "mysql" % "mysql-connector-java" % "5.1.34",
  specs2 % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.amazonaws" % "aws-lambda-java-core" % "1.0.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.0.0"
)

assemblyMergeStrategy in assembly <<= (assemblyMergeStrategy in assembly) {
  (old) => {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}
