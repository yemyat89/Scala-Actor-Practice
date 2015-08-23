name := "Practice_Actor"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-remote" % "2.3.4"
)

//mainClass in Compile := Some("runner.ServerRunnerMain1")

//mainClass in Compile := Some("client.ClientMain")

mainClass in Compile := Some("cli.MainCLI")

