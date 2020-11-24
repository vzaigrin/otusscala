name := "l10"

version := "1.0"

scalaVersion := "2.13.3"

lazy val root = (project in file(".")).settings(scalacOptions += "-deprecation")

parallelExecution in Test := false

logBuffered := false

lazy val akkaHttpVersion         = "10.2.1"
lazy val akkaVersion             = "2.6.10"
lazy val akkaHttpPlayJsonVersion = "1.34.0"
lazy val PlayJsonVersion         = "2.9.0"
lazy val tapirVersion            = "0.17.0-M2"
val circeVersion                 = "0.12.3"

libraryDependencies ++= Seq(
  "com.typesafe"                 % "config"                     % "1.4.0",
  "com.typesafe.akka"           %% "akka-http"                  % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-http-testkit"          % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-actor-typed"           % akkaVersion,
  "com.typesafe.akka"           %% "akka-stream"                % akkaVersion,
  "com.typesafe.akka"           %% "akka-stream-testkit"        % akkaVersion,
  "com.typesafe.slick"          %% "slick"                      % "3.3.3",
  "com.typesafe.slick"          %% "slick-hikaricp"             % "3.3.3",
  "org.slf4j"                    % "slf4j-nop"                  % "1.6.4",
  "org.postgresql"               % "postgresql"                 % "42.2.16",
  "com.softwaremill.sttp.tapir" %% "tapir-core"                 % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"           % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,
//  "io.circe"                    %% "circe-core"                 % circeVersion,
//  "io.circe"                    %% "circe-generic"              % circeVersion,
//  "io.circe"                    %% "circe-parser"               % circeVersion,
  "com.pauldijou"              %% "jwt-circe"                 % "4.2.0",
  "org.scalatest"              %% "scalatest"                 % "3.2.0"   % Test,
  "org.scalamock"              %% "scalamock"                 % "5.0.0"   % Test,
  "org.scalacheck"             %% "scalacheck"                % "1.14.3"  % Test,
  "org.scalatestplus"          %% "scalacheck-1-14"           % "3.2.0.0" % Test,
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3"   % Test
)
