package com.adoption_agency

class GitUtils implements Serializable {

    def script

    GitUtils(script) {
        this.script = script
    }

    String bumpAndTag(String credentialsId = 'github-push-creds') {
        def result

        script.withCredentials([
            script.usernamePassword(
                credentialsId: credentialsId,
                usernameVariable: 'GIT_USER',
                passwordVariable: 'GIT_TOKEN'
            )
        ]) {
            script.sh '''
                git config user.name "jenkins-bot"
                git config user.email "jenkins-bot@ci.local"
                git config credential.helper '!f() { echo "username=${GIT_USER}"; echo "password=${GIT_TOKEN}"; }; f'

                git fetch --tags --force
            '''

            def latestTag = script.sh(
                script: "git tag --list 'v*.*.*' --sort=-version:refname | head -n 1",
                returnStdout: true
            ).trim()

            if (!latestTag) {
                latestTag = 'v0.0.0'
            }

            def version = latestTag.substring(1)
            def parts = version.split('\\.')

            if (parts.size() != 3) {
                script.error("Invalid version tag: ${latestTag}")
            }

            def major = parts[0].toInteger()
            def minor = parts[1].toInteger()
            def patch = parts[2].toInteger() + 1

            def newTag = "v${major}.${minor}.${patch}"

            script.sh """
                git tag -a ${newTag} -m "Release ${newTag}"
                git push origin ${newTag}
            """

            result = "${major}.${minor}.${patch}"
        }

        return result
    }
}