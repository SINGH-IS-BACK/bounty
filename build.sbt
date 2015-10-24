name := "Trump"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "2.10.1"

libraryDependencies += "org.facebook4j" % "facebook4j-core" % "2.2.0"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.5"

libraryDependencies += "com.sun.jersey" % "jersey-client" % "1.17.1"

libraryDependencies += "com.sun.jersey" % "jersey-core" % "1.17.1"

libraryDependencies += "com.sun.jersey.contribs" % "jersey-multipart" % "1.17.1"

play.Project.playJavaSettings


fork in run := true