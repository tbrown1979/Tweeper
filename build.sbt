scalaVersion := "2.11.8"

libraryDependencies ++= {
  val scalaTestV  = "2.2.5"
  val version = "0.14.3"
  //val rhoVersion = "0.7.0"
  Seq(
//    "org.scalatest"          %% "scalatest"        % scalaTestV % "test",
    //"org.scalaz.stream"      %% "scalaz-stream"    % "0.8",
    "com.codahale.metrics"   %  "metrics-core"     % "3.0.1",
    "org.specs2"             %% "specs2-core"      % "2.4.2" % "test",
    "org.twitter4j"          %  "twitter4j-stream" % "4.0.2",
    "com.github.nscala-time" %% "nscala-time"      % "1.4.0",
    "jsonz"                  %% "jsonz"            % "1.2.0",
    "co.fs2" %% "fs2-core" % "0.9.0-RC2",

  "org.http4s" %% "http4s-dsl"          % version,  // to use the core dsl
    "org.http4s" %% "http4s-blaze-server" % version,  // to use the blaze backend,
    "org.http4s" %% "http4s-servlet"      % version,  // to use the raw servlet backend
    "org.http4s" %% "http4s-jetty"        % version,  // to use the jetty servlet backend
    "org.http4s" %% "http4s-blaze-client" % version,  // to use the blaze client
    "com.typesafe" % "config" % "1.3.0",
  "org.tpolecat" %% "doobie-core" % "0.3.0"
  )
}

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
resolvers += "bintray-banno-oss-releases" at "http://dl.bintray.com/banno/oss"


enablePlugins(JavaAppPackaging)

Revolver.settings
