/**
 * High-level pipeline that supports multiple Python projects and Dockerfiles.
 *
 * Parameters:
 *   pythonProjects  - List of maps: [path: '...', name: '...']
 *   dockerProjects  - List of maps: [path: '...', name: '...']
 *   context         - Docker build context (default: '.')
 *   tag             - Image tag (default: BUILD_NUMBER)
 *   extraArgs       - Extra args for Python steps
 */
def call(Map config = [:]) {

    def pythonProjects = config.pythonProjects ?: []
    def dockerProjects = config.dockerProjects ?: []
    // def context        = config.context        ?: '.'
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

            // ---------- Python ----------
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

            // ---------- Docker ----------
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
                            // def context = project.context

                            echo "=== Docker image: ${name} (${path}) ==="
                            buildDocker(
                                dockerfilePath: path,
                                imageName: name,
                                // context: context,
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