/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import play.twirl.sbt.Import.TwirlKeys
import play.twirl.sbt.SbtTwirl
import sbt.Build

import de.heikoseeberger.sbtheader.SbtHeader.autoImport._
import sbt._
import sbt.Keys._

object HmrcBuild extends Build {

  import uk.gov.hmrc.DefaultBuildSettings
  import uk.gov.hmrc.PublishingSettings._
  import DefaultBuildSettings._
  import uk.gov.hmrc.{SbtBuildInfo, ShellPrompt}
  import Dependencies._

  val appName = "play-ui"
  val appVersion = "1.10.0-SNAPSHOT"

  val appDependencies = Seq(
    Compile.play,
    Compile.playFilters,

    Test.scalaTest,
    Test.pegdown,
    Test.jsoup,
    Test.playTest
  )

  lazy val playUi = (project in file("."))
    .settings(name := appName)
    .settings(version := appVersion)
    .settings(scalaSettings : _*)
    .settings(defaultSettings() : _*)
    .settings(
      targetJvm := "jvm-1.7",
      shellPrompt := ShellPrompt(appVersion),
      libraryDependencies ++= appDependencies,
      resolvers := Seq(
        Opts.resolver.sonatypeReleases,
        Opts.resolver.sonatypeSnapshots,
        "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/",
        "typesafe-snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
      ),
      crossScalaVersions := Seq("2.11.6")
    )
    .settings(publishAllArtefacts: _*)
    .settings(SbtBuildInfo(): _*)
    .enablePlugins(SbtTwirl)
    .settings(TwirlKeys.templateImports ++= Seq("play.api.mvc._", "play.api.data._", "play.api.i18n._", "play.api.templates.PlayMagic._"))
    .settings(unmanagedSourceDirectories in sbt.Compile += baseDirectory.value / "src/main/twirl")
    .settings(Headers(): _ *)
    .settings(POMMetadata(): _*)

}

object Dependencies {
  import _root_.play.core.PlayVersion
  object Compile {
    val play = "com.typesafe.play" %% "play" % PlayVersion.current % "provided"
    val playFilters = "com.typesafe.play" %% "filters-helpers" % PlayVersion.current % "provided"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.4.2" % scope
    val jsoup = "org.jsoup" % "jsoup" % "1.7.2" % scope
    val playTest = "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
  }

  object Test extends Test("test")
}

object Headers {
  import de.heikoseeberger.sbtheader.SbtHeader.autoImport._
  def apply() = Seq(
    headers := Map(
      "scala" ->(
        HeaderPattern.cStyleBlockComment,
        """|/*
          | * Copyright 2015 HM Revenue & Customs
          | *
          | * Licensed under the Apache License, Version 2.0 (the "License");
          | * you may not use this file except in compliance with the License.
          | * You may obtain a copy of the License at
          | *
          | *   http://www.apache.org/licenses/LICENSE-2.0
          | *
          | * Unless required by applicable law or agreed to in writing, software
          | * distributed under the License is distributed on an "AS IS" BASIS,
          | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
          | * See the License for the specific language governing permissions and
          | * limitations under the License.
          | */
          |
          |""".stripMargin
        )
    ),
    (compile in Compile) <<= (compile in Compile) dependsOn (createHeaders in Compile),
    (compile in Test) <<= (compile in Test) dependsOn (createHeaders in Test)
  )
}


object POMMetadata {
  def apply() = {
      pomExtra :=
        <url>https://www.gov.uk/government/organisations/hm-revenue-customs</url>
          <licenses>
            <license>
              <name>Apache 2</name>
              <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            </license>
          </licenses>
          <scm>
            <connection>scm:git@github.com:hmrc/play-ui.git</connection>
            <developerConnection>scm:git@github.com:hmrc/play-ui.git</developerConnection>
            <url>git@github.com:hmrc/play-ui.git</url>
          </scm>
          <developers>
            <developer>
              <id>steve-e</id>
              <name>Steve Etherington</name>
              <url>http://www.equalexperts.com/</url>
            </developer>
          </developers>
  }
}

