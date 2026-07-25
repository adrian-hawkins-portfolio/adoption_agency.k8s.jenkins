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
            def repoName = Utils.getRepoName(this).tokenize('.').last()
            echo "Repository: ${repoName}"
            sh 'helm version'
            sh 'helm list -A'
            sh 'git clone https://github.com/adrian-hawkins-portfolio/adoption_agency.k8s.helm.git'
            echo "building ${repoName}/${imageName}"
            sh """
                helm dependency build ./adoption_agency.k8s.helm/${repoName}/${imageName}
                helm template \
                ./adoption_agency.k8s.helm/${repoName}/${imageName} \
                --values ./adoption_agency.k8s.helm/${repoName}/${imageName}/values.yaml
            """
        }
    }
}