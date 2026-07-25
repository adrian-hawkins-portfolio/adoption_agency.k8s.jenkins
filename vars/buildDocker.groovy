def call(Map config = [:]) {
    def dockerfilePath = config.dockerfilePath ?: 'Dockerfile'
    def imageName      = config.imageName ?: env.JOB_NAME.replaceAll('/', '-')
    def context        = config.context ?: '.'
    def tag            = config.tag ?: env.BUILD_NUMBER

    stage('Docker - Info') {
        sh 'docker info'
    }
}