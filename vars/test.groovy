def call(Map params) {
        def buildStep = {
            sh 'npm run build'
        }
}
