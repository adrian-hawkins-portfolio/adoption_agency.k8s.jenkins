package com.adoption_agency

class Utils implements Serializable {

    static String getRepoName(script) {
        return script.sh(
            script: '''
                basename -s .git "$(git config --get remote.origin.url)"
            ''',
            returnStdout: true
        ).trim()
    }
}