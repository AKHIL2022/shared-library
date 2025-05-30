def call() {
        def buildStep = {
            sh 'npm run build'
        }
}
