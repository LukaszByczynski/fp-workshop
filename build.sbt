name := "fp-workshop"
version := "1.0"
scalaVersion := "2.12.6"

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-Ypartial-unification"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

libraryDependencies ++= {
  val CatsEffect = Seq(
    "org.typelevel" %% "cats-effect" % "1.0.0-RC2"
  )

  val CatsMtl = Seq(
    "org.typelevel" %% "cats-mtl-core" % "0.2.1",
    "com.olegpy" %% "meow-mtl" % "0.1.1"
  )

  val Http4s = Seq(
    "org.http4s"      %% "http4s-blaze-server" % "0.19.0-M1",
    "org.http4s"      %% "http4s-circe"        % "0.19.0-M1",
    "org.http4s"      %% "http4s-dsl"          % "0.19.0-M1",
    // Optional for auto-derivation of JSON codecs
    "io.circe" %% "circe-generic" % "0.9.3",
    // Optional for string interpolation to JSON model
    "io.circe" %% "circe-literal" % "0.9.3"
  )

  val ScalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

  val Logback = Seq(
    "ch.qos.logback"  %  "logback-classic"     % "1.2.3"
  )

  CatsEffect ++ CatsMtl ++ Http4s ++ Logback ++ ScalaTest
}