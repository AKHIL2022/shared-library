def call(Map params) {
    if (params.hasRelevantChanges || force_build) {
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
