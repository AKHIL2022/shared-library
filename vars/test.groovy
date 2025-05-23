def call(Map params) {
    echo "hasRelevantChanges: ${params.HasRelevantChanges}"
    echo "force: ${params.force_build}"
    if (params.HasRelevantChanges || params.force_build) {
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
