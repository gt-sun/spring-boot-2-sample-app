import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.1"

project {
    defaultTemplate = AbsoluteId("CarlTest_BeforeScript")

    buildType(Test)
    buildType(Jboss)
    buildType(Prod)
    buildType(Build1)
    buildType(Qa)
    buildTypesOrder = arrayListOf(Build1, Qa, Prod)
}

object Build1 : BuildType({
    name = "Build"

    enablePersonalBuilds = false
    artifactRules = "target/spring-boot-sample-actuator-2.0.2.jar"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "mvnBuild"
            id = "RUNNER_2"
            goals = "package"
            mavenVersion = bundled_3_5()
            userSettingsSelection = "settings.xml"
        }
    }
    
    disableSettings("RUNNER_6")
})

object Jboss : BuildType({
    name = "jboss"

    steps {
        script {
            id = "RUNNER_8"
            workingDir = "/data/jboss/jboss-eap-7.1"
            scriptContent = """
                pwd
                ls -al
                echo add from VCS settings.kts
                bin/jboss-cli.sh -c --controller=cnjbossqa01.mmm.com:9990 -u=%env.cnjbossqa01_user% -p=%env.cnjbossqa01_pwd% --command="deployment-info"
            """.trimIndent()
        }
    }
    
    disableSettings("RUNNER_6")
})

object Prod : BuildType({
    name = "prod"

    enablePersonalBuilds = false
    artifactRules = "spring-boot-sample-actuator*.jar"
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        text("PWD", "******", description = "Input admin passwd", display = ParameterDisplay.PROMPT,
              regex = "111111", validationMessage = "invalidate")
    }

    steps {
        script {
            id = "RUNNER_5"
            scriptContent = """
                pwd
                ls -al
                echo "Build Step prod"
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Qa) {
            snapshot {
                runOnSameAgent = true
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_2"
                cleanDestination = true
                artifactRules = "spring-boot-sample-actuator*.jar"
            }
        }
    }
})

object Qa : BuildType({
    name = "qa"

    enablePersonalBuilds = false
    artifactRules = "spring-boot-sample-actuator*.jar"
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    steps {
        script {
            id = "RUNNER_5"
            scriptContent = """
                pwd
                ls -al
                echo "Build Step qa"
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Build1) {
            snapshot {
                runOnSameAgent = true
                reuseBuilds = ReuseBuilds.NO
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_2"
                artifactRules = "spring-boot-sample-actuator*.jar"
            }
        }
    }
    
    disableSettings("RUNNER_6")
})

object Test : BuildType({
    name = "test"

    steps {
        script {
            name = "test_multiline_script"
            id = "RUNNER_12"
            scriptContent = "echo %teamcity.build.triggeredBy%"
        }
    }
    
    disableSettings("RUNNER_6")
})
