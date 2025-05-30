def call(String localFolderName) {
    def changes = []
    def build = currentBuild
    def returnValues = []
    def relevant
    def isPackageJsonChanged
    def hasRelevantChanges
    while (build != null && build.result != 'SUCCESS') {
        for (changeLog in build.changeSets) {
            for (entry in changeLog.items) {
                for (file in entry.affectedFiles) {
                    changes += file.path
                }
            }
        }
        build = build.previousBuild
        if (!build) {
            changes = ["${localFolderName}/*"]
        }
    }
    changes.unique().sort()
    echo "Changed since last successful build: ${changes.isEmpty() ? 'none' : changes.join(', \n')}"
    relevant = changes.findAll { element ->
        // Include changes to our localFolderPath
        element ==~ /\Q$localFolderName\E\/.*/
    }
    relevant = relevant.findAll { element ->
        // Ignore changes to *.test.js files
        !(element ==~ /.*\.test\.js/)
    }
    isPackageJsonChanged = relevant.any { element ->
        element ==~ /.*package-lock\.json/
    }
    hasRelevantChanges = !relevant.isEmpty()
    if (hasRelevantChanges) {
        echo "There are changes that affect the deployment: ${Relevant.join(', ')}"
    } else {
        echo 'There are no changes that would affect the deployment'
    }
    returnValues = [
        isPackageJsonChanged: isPackageJsonChanged.toString(),
        hasRelevantChanges: hasRelevantChanges.toString()
    ]
    return returnValues
}
