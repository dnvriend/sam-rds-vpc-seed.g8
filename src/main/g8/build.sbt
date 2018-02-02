
lazy val `$name$` = (project in file("."))
  .settings(
	  libraryDependencies += "com.github.dnvriend" %% "sam-annotations" % "1.0.20",
    libraryDependencies += "com.github.dnvriend" %% "sam-lambda" % "1.0.20",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
    libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.8",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.0",
    libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.0",
    libraryDependencies += "org.playframework.anorm" %% "anorm-postgres" % "2.6.0",
    libraryDependencies += "com.zaxxer" % "HikariCP" % "2.7.6",
    libraryDependencies += "com.typesafe" % "config" % "1.3.2",
    resolvers += Resolver.bintrayRepo("dnvriend", "maven"),
    scalaVersion := "2.12.4",
	  samStage := "$stage$",
	  organization := "$organization$",
	  description := "simple sam rds setup with vpc and api handler"
  )

  

