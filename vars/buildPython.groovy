def call(Map config = [:]) {
    def pyprojectPath = config.pyprojectPath ?: 'pyproject.toml'
    def projectDir = pyprojectPath.contains('/') 
        ? pyprojectPath.substring(0, pyprojectPath.lastIndexOf('/')) 
        : '.'

    stage('Poetry setup') {
        sh """
        poetry --version
        poetry config repositories.azure $PYPI_URL
        poetry config http-basic.azure azure $PYPI_TOKEN
        """
    }


    stage('Poetry publish') {
        // Horrible version strategy
        sh """
            cd ${projectDir}
            poetry version ${env.BUILD_NUMBER}
            poetry build
            poetry publish -r azure
        """
    }
}