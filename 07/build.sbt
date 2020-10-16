name := "l07"

version := "1.0"

scalaVersion := "2.13.3"

lazy val root = (project in file(".")).settings(scalacOptions += "-deprecation")

parallelExecution in Test := false

logBuffered := false

lazy val akkaHttpVersion         = "10.2.1"
lazy val akkaVersion             = "2.6.10"
lazy val akkaHttpPlayJsonVersion = "1.34.0"
lazy val PlayJsonVersion         = "2.9.0"

libraryDependencies ++= Seq(
  "com.typesafe"                % "config"                    % "1.4.0",
  "com.typesafe.akka"          %% "akka-http"                 % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-actor-typed"          % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream"               % akkaVersion,
  "de.heikoseeberger"          %% "akka-http-play-json"       % akkaHttpPlayJsonVersion,
  "com.typesafe.play"          %% "play-json"                 % PlayJsonVersion,
  "com.typesafe.slick"         %% "slick"                     % "3.3.3",
  "com.typesafe.slick"         %% "slick-hikaricp"            % "3.3.3",
  "org.slf4j"                   % "slf4j-nop"                 % "1.6.4",
  "org.postgresql"              % "postgresql"                % "42.2.16",
  "com.typesafe.akka"          %% "akka-stream-testkit"       % akkaVersion,
  "com.typesafe.akka"          %% "akka-http-testkit"         % akkaHttpVersion,
  "org.scalatest"              %% "scalatest"                 % "3.2.0"   % Test,
  "org.scalamock"              %% "scalamock"                 % "5.0.0"   % Test,
  "org.scalacheck"             %% "scalacheck"                % "1.14.3"  % Test,
  "org.scalatestplus"          %% "scalacheck-1-14"           % "3.2.0.0" % Test,
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3"   % Test
)
