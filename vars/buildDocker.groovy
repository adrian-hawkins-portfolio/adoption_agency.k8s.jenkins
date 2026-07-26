import com.adoption_agency.Utils

def call(Map config = [:]) {
    def dockerfilePath = config.dockerfilePath ?: 'Dockerfile'
    def imageName      = config.imageName ?: env.JOB_NAME.replaceAll('/', '-')
    def context        = config.context ?: '.'
    def tag            = config.tag ?: env.BUILD_NUMBER
    def isPod          = config.isPod ?: false

    stage('Docker - Info') {
        sh 'docker info'
        sh """
            docker build \
              --build-arg AZURE_DEVOPS_PAT=$PYPI_TOKEN \
              --build-arg AZURE_FEED_URL=$PYPI_URL_PULL \
              -t ghcr.io/adrian-hawkins-portfolio/${imageName}:${tag} ${dockerfilePath}
            docker push ghcr.io/adrian-hawkins-portfolio/${imageName}:${tag}
        """
    }

    if (isPod) {
        stage('Helm') {
            def repoName = Utils.getRepoName(this).tokenize('.').last()
            echo "Repository: ${repoName}"
            sh 'helm version'
            sh 'helm list -A'
            sh """
                if [ ! -d "adoption_agency.k8s.helm" ]; then
                    git clone https://github.com/adrian-hawkins-portfolio/adoption_agency.k8s.helm.git
                fi
            """
            echo "building ${repoName}/${imageName}"
            sh """
                helm dependency build ./adoption_agency.k8s.helm/${repoName}/${imageName}
                helm upgrade --install ${imageName} \
                    ./adoption_agency.k8s.helm/${repoName}/${imageName} \
                    --values ./adoption_agency.k8s.helm/${repoName}/${imageName}/values.yaml \
                    -n prod \
                    --set base.image.tag=${tag}
            """
        }
    }
}