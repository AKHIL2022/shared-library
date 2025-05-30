def call(Map params) {
    echo "hasRelevantChanges: ${params.HasRelevantChanges}"
    echo "force: ${params.force_build}"
        def buildStep = {
            sh 'npm run build'
        }
}
