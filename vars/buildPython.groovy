def call(Map config = [:]) {
    def pyprojectPath = config.pyprojectPath ?: 'pyproject.toml'
    def extraArgs     = config.extraArgs ?: ''

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

            stage('Test Tools') {
                steps {
                    sh 'poetry --version'
                }
            `}
        }

        post {
            always {
                cleanWs()
            }
        }
    }
}