def call(Map params) {
    echo "hasRelevantChanges: ${params.hasRelevantChanges}"
    echo "force: ${params.force_build}"
    if (params.hasRelevantChanges || params.force_build) {
        def buildStep = {
            sh 'npm run build'
        }
        if (params.localFolder) {
            dir(params.localFolder) {
                buildStep()
            }
        } else {
            buildStep()
        }
    }
}
