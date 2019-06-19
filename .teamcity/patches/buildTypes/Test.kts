package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Test'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Test")) {
    params {
        add {
            param("env.admin_users", "a9k47zz aa bb cc")
        }
        add {
            param("env.cnjbossprod01_user", "carl2222")
        }
    }

    expectSteps {
        script {
            name = "test_multiline_script"
            scriptContent = "echo %teamcity.build.triggeredBy%"
        }
    }
    steps {
        update<ScriptBuildStep>(0) {
            scriptContent = """
                users=(%env.admin_users%)
                echo ${'$'}{users[@]}
                echo ${'$'}{#users[@]}
                last_name=${'$'}{users[${'$'}((${'$'}{#users[@]}-1))]}
                
                for i in ${'$'}{admin_users[@]}
                do 
                  if [ "%teamcity.build.triggeredBy.username%" == ${'$'}i ];then 
                    echo permission check pass ^_^
                    break
                  fi
                  if [ "%teamcity.build.triggeredBy.username%" != ${'$'}i ] && [ ${'$'}i == "${'$'}{last_name}" ]
                  then
                    echo -e "\033[41;37m You don't have permission to execute this for PROD environment!! EXIT... \033[0m"
                    echo "Please connect to system admin, Sting Lu or Carl Sun"
                    exit 1
                  fi
                done
            """.trimIndent()
        }
    }
}
