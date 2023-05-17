def jenkinsfile = '''
@Library("devops-gsl-pipelines") _
e2eTest(
)
'''

pipelineJob('tests/nh-template-ui/nh-template-ui-staging-desktop') {
    description('Running nhTemplateUiTest, desktop view')

    definition {
        cps {
            script(jenkinsfile)
            sandbox(true)
        }
    }

    parameters {
        stringParam('dockerImage', '628232394424.dkr.ecr.eu-central-1.amazonaws.com/web-tests:master', 'Docker image to run')
        stringParam('command', './gradlew -Penv=staging-headless -PmaxFailuresForRetry=300 -PmaxRetries=3 --continue nhTemplateUiTest allureReport', 'Command to run in a container')
        choiceParam('environment', ['tst', 'dev', 'ppd', 'prd', 'shared', 'none'], 'Configure access to environment. It enables the access to account that hold secrets (the "shared" account should not be confused with "/shared/*" paths of secrets!)')
        stringParam('junitReportPath', '', 'Path to JUnit report directory')
        stringParam('htmlReportPath', '/workspace/build/reports/allure-report', 'Path to HTML report directory')
        stringParam('archiveArtifactsPath', '/workspace/build/reports/tests', 'Archive file path')
        textParam('environmentVars', '', 'Additional environmental variables in NAME=VALUE format, separated by new lines.')
        stringParam('customConfigPath', '', 'Additional config file path')
        textParam('customConfigContent', '', 'Additional config file content')
        stringParam('customBuildName', '', 'Custom Jenkins build display name. When is empty it will be fill with last git commit name.')
        stringParam('slackChannels', 'web-tests-results', 'Slack channel where Junit test results will be sent, separated by comma')
        stringParam('slackUsers', '@laap-team-qa', 'Users that will be mentioned in Slack message with Junit test results, like @slacknickname')
        stringParam('timeout', '30', 'Inactivity time limit in minutes - job will automatically close when there is no log activity for this period')
        stringParam('allureServerProjectName', 'nh-template-ui-staging-desktop', 'Name of the project to store it remotely on the allure server')
        stringParam('moonSessionCount', '0', 'Allocate N Moon Cloud sessions. Value bigger than 0 tells that it should monitor the state of Moon Cloud and' +
                ' don\'t start running tests if there is not enough capacity for it. NOTE: default web-tests parallelism will be overridden by this!')
        stringParam('browserAllocationTimeout', '120', 'Timeout in minutes for allocating browser sessions in Moon Cloud')
    }

    logRotator {
        numToKeep(50)
        daysToKeep(10)
    }

    triggers {
        cron('0 4 * * *')
    }
}

