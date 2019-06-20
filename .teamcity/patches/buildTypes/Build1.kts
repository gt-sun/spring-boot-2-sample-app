package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build1'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build1")) {
    expectSteps {
        maven {
            name = "mvnBuild"
            goals = "package"
            mavenVersion = bundled_3_5()
            userSettingsSelection = "settings.xml"
        }
    }
    steps {
        items.removeAt(0)
    }

    expectDisabledSettings("RUNNER_6")
    updateDisabledSettings("RUNNER_27", "RUNNER_28", "RUNNER_29", "RUNNER_6")
}
