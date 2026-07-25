import com.adoption_agency.Utils

def call(Map config = [:]) {
    def dockerfilePath = config.dockerfilePath ?: 'Dockerfile'
    def imageName      = config.imageName ?: env.JOB_NAME.replaceAll('/', '-')
    def context        = config.context ?: '.'
    def tag            = config.tag ?: env.BUILD_NUMBER
    def isPod          = config.isPod ?: false

    stage('Docker - Info') {
        sh 'docker info'
    }

    if (isPod) {
        stage('Helm') {
            def repoName = Utils.getRepoName(this)
            echo "Repository: ${repoName}"
            sh 'helm version'
        }
    }
}