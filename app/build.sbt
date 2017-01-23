scalaVersion := "2.11.8"

name := "prometheus-alertmanager-example-app"

organization := "com.example"

version := "0.0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-netty-server" % "2.5.10",
  "io.prometheus" % "simpleclient" % "0.0.19",
  "io.prometheus" % "simpleclient_servlet" % "0.0.10"
)

enablePlugins(
  JavaAppPackaging,
  DockerComposePlugin
)

dockerBaseImage := "anapsix/alpine-java:8_server-jre"
dockerUpdateLatest := true
dockerImageCreationTask := (publishLocal in Docker).value
composeFile := "../docker-compose.yml"
composeServiceName := name.value

