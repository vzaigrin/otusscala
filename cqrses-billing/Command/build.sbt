name := "Command"

version := "1.0"

scalaVersion := "2.13.4"

resolvers += Resolver.bintrayRepo("akka", "snapshots")

lazy val akkaHttpVersion         = "10.2.1"
lazy val akkaVersion             = "2.6.10"
lazy val akkaHttpPlayJsonVersion = "1.34.0"
lazy val PlayJsonVersion         = "2.9.0"
lazy val tapirVersion            = "0.17.0-M2"

libraryDependencies ++= Seq(
  "com.typesafe"                 % "config"                     % "1.4.0",
  "com.typesafe.akka"           %% "akka-http"                  % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-http-testkit"          % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-actor-typed"           % akkaVersion,
  "com.typesafe.akka"           %% "akka-stream"                % akkaVersion,
  "com.typesafe.akka"           %% "akka-stream-testkit"        % akkaVersion,
  "com.typesafe.akka"           %% "akka-persistence-cassandra" % "1.0.4",
  "com.typesafe.akka"           %% "akka-persistence-typed"     % akkaVersion,
  "com.typesafe.akka"           %% "akka-persistence"           % akkaVersion,
  "com.typesafe.akka"           %% "akka-persistence-query"     % akkaVersion,
  "com.typesafe.akka"           %% "akka-cluster-tools"         % akkaVersion,
  "org.slf4j"                    % "slf4j-nop"                  % "1.6.4",
  "com.softwaremill.sttp.tapir" %% "tapir-core"                 % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"           % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,
  "org.scalatest"               %% "scalatest"                  % "3.2.0"  % Test,
  "org.scalamock"               %% "scalamock"                  % "5.0.0"  % Test,
  "org.scalacheck"              %% "scalacheck"                 % "1.14.3" % Test
)
