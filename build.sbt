name := "fp-workshop"
version := "1.0"
scalaVersion := "2.12.7"

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-Ypartial-unification"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")

libraryDependencies ++= {
  val CatsEffect = Seq(
    "org.typelevel" %% "cats-effect" % "1.0.0"
  )

  val CatsMtl = Seq(
    "org.typelevel" %% "cats-mtl-core" % "0.4.0",
    "com.olegpy" %% "meow-mtl" % "0.1.3"
  )

  val Http4s = Seq(
    "org.http4s" %% "http4s-blaze-server" % "0.19.0-M3",
    "org.http4s" %% "http4s-circe" % "0.19.0-M3",
    "org.http4s" %% "http4s-dsl" % "0.19.0-M3",
    // Optional for auto-derivation of JSON codecs
    "io.circe" %% "circe-generic" % "0.10.0",
    // Optional for string interpolation to JSON model
    "io.circe" %% "circe-literal" % "0.10.0"
  )

  val ScalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

  val Logback = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  CatsEffect ++ CatsMtl ++ Http4s ++ Logback ++ ScalaTest
}