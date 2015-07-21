scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "com.typesafe.akka"      %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"      %%  "akka-testkit"      % akkaV  % "test",
    "org.specs2"             %% "specs2-core" % "2.3.11" % "test",
    "org.twitter4j"          %   "twitter4j-stream"  % "4.0.2",
    "io.spray"               %%   "spray-can"         % sprayV,
    "io.spray"               %%   "spray-routing"     % sprayV,
    "io.spray"               %%   "spray-client"      % sprayV,
    "io.spray"               %%   "spray-testkit"     % sprayV,
    "io.spray"               %%   "spray-json"   % "1.3.2",
    "com.github.nscala-time" %%  "nscala-time"       % "1.4.0",
    "com.codahale.metrics"   %   "metrics-core"      % "3.0.1",
    "org.elasticsearch"      %   "metrics-elasticsearch-reporter" % "2.0"
  )
}

// libraryDependencies ++= {
//   val akkaV = "2.3.9"
//   val sprayV = "1.3.3"
//   Seq(
//     "io.spray"            %%  "spray-can"     % sprayV,
//     "io.spray"            %%  "spray-routing" % sprayV,
//     "io.spray"            %%  "spray-testkit" % sprayV  % "test",
//     "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
//     "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
//     "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test"
//   )
// }



enablePlugins(JavaAppPackaging)

Revolver.settings
