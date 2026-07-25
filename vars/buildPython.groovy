def call(Map config = [:]) {
    def pyprojectPath = config.pyprojectPath ?: 'pyproject.toml'
    def extraArgs     = config.extraArgs ?: ''

    stage('Python - Tools') {
        sh 'poetry --version'
    }
}