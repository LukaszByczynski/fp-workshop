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

  CatsEffect
}