def call(Map config = [:]) {

    def pythonProjects = config.pythonProjects ?: []
    def dockerProjects = config.dockerProjects ?: []
    def tag            = config.tag            ?: env.BUILD_NUMBER
    def extraArgs      = config.extraArgs      ?: ''

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
                    expression { return pythonProjects }
                }
                steps {
                    script {
                        pythonProjects.each { project ->
                            def path = project.path
                            def name = project.name ?: path

                            echo "=== Python project: ${name} (${path}) ==="
                            buildPython(
                                pyprojectPath: path,
                                extraArgs: extraArgs
                            )
                        }
                    }
                }
            }

            stage('Docker') {
                when {
                    expression { return dockerProjects }
                }
                steps {
                    script {
                        dockerProjects.each { project ->
                            def path  = project.path
                            def name  = project.name ?: env.JOB_NAME.replaceAll('/', '-')
                            def isPod = project.isPod ?: false

                            echo "=== Docker image: ${name} (${path}) ==="
                            buildDocker(
                                dockerfilePath: path,
                                imageName: name,
                                tag: tag,
                                isPod: isPod
                            )
                        }
                    }
                }
            }
        }
    }
}