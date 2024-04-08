scalaVersion := "2.13.12"

enablePlugins(ScalaNativePlugin)

// set to Debug for compilation details (Info is default)
logLevel := Level.Info

// import to add Scala Native options
import scala.scalanative.build._

libraryDependencies ++= Seq(
  "org.ekrich" %%% "sconfig" % "1.6.0",
  "io.chrisdavenport" %%% "cats-effect-time" % "0.2.1",
  "com.monovore" %%% "decline" % "2.4.1",
  "com.monovore" %%% "decline-effect" % "2.4.1",
  "org.typelevel" %%% "cats-core" % "2.10.0",
  "org.typelevel" %%% "cats-effect" % "3.5.4",
  "co.fs2" %%% "fs2-core" % "3.10.2",
  "co.fs2" %%% "fs2-io" % "3.10.2"
)

// defaults set with common options shown
nativeConfig ~= { c =>
  c.withLTO(LTO.none) // thin
    .withMode(Mode.debug) // releaseFast
    .withGC(GC.immix) // commix
}

nativeLinkStubs := true
