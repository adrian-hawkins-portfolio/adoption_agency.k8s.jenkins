def call(Map config = [:]) {
    def pyprojectPath = config.pyprojectPath ?: 'pyproject.toml'
    def extraArgs     = config.extraArgs ?: ''

    stage('Python - Tools') {
        sh 'poetry --version'
        sh 'echo "PYPI_URL = $PYPI_URL"'
    }
}