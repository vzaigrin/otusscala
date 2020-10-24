scalaVersion := "2.13.3"

name := "l04"

version := "1.0"

lazy val root = (project in file("."))
  .settings(
    scalacOptions += "-deprecation"
  )

libraryDependencies ++= Seq(
  "org.scalactic"     %% "scalactic"       % "3.2.0"   % Test,
  "org.scalatest"     %% "scalatest"       % "3.2.0"   % Test,
  "org.scalacheck"    %% "scalacheck"      % "1.14.3"  % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.0.0" % Test,
  "org.scalamock"     %% "scalamock"       % "5.0.0"   % Test
)
