scalaVersion := "2.10.2"

libraryDependencies ++= {
  val akkaV = "2.1.4"
  val sprayV = "1.1.1"
  Seq(
    "com.typesafe.akka"      %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"      %%  "akka-testkit"      % akkaV   % "test",
    "org.specs2"             %%  "specs2-core"       % "2.3.9" % "test",
    "org.twitter4j"          %   "twitter4j-stream"  % "4.0.2",
    "com.sksamuel.elastic4s" %%  "elastic4s"         % "1.2.1.2",
    "io.spray"               %   "spray-can"         % sprayV,
    "io.spray"               %   "spray-routing"     % sprayV,
    "io.spray"               %   "spray-client"      % sprayV,
    "io.spray"               %   "spray-testkit"     % sprayV,
    "io.spray"               %   "spray-json_2.10"   % "1.2.5",
    "com.github.nscala-time" %%  "nscala-time"       % "1.4.0",
    "com.codahale.metrics"   %   "metrics-core"      % "3.0.1"
  )
}

scalacOptions += "-deprecation"
