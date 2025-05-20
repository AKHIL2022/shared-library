def call(Map params) {
    if (hasRelevantChanges || force_build) {
        def buildStep = {
            sh 'npm run build'
        }

        if (params.localFolder) {
            dir(params.localFolder, buildStep)
        } else {
            buildStep()
        }
    }
}
