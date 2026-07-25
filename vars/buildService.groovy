def call(Map config = [:]) {

    pipeline {
        agent {
            label 'docker-agent'
        }

        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Python') {
                when {
                    expression { return config.pyprojectPath }
                }
                steps {
                    buildPython(
                        pyprojectPath: config.pyprojectPath,
                        extraArgs: config.extraArgs
                    )
                }
            }

            stage('Docker') {
                when {
                    expression { return config.dockerfilePath }
                }
                steps {
                    buildDocker(
                        dockerfilePath: config.dockerfilePath,
                        imageName: config.imageName,
                        context: config.context,
                        tag: config.tag
                    )
                }
            }
        }
    }
}