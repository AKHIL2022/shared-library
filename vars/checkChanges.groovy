def call(String localFolderName) {
    def changes = []
    def build = currentBuild
    def relevant = []
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
            changes = ["${localFolderName}/package.json"]  // Assume package.json changed if no prior builds
        }
    }
    changes = changes.unique().sort()
    echo "Changes since last successful build: ${changes.isEmpty() ? 'none' : changes.join(', \n')}"

    relevant = changes.findAll { element ->
        element ==~ /\Q${localFolderName}\E\/.*/  // Match floward-exercise/*
    }.findAll { element ->
        !(element ==~ /.*\.test\.js/)  // Exclude test files
    }

    echo "Relevant changes: ${relevant.isEmpty() ? 'none' : relevant.join(', \n')}"

    isPackageJsonChanged = relevant.any { element ->
        element ==~ /.*package\.json/  // Match package.json
    }

    hasRelevantChanges = !relevant.isEmpty()

    if (hasRelevantChanges) {
        echo "There are changes that affect the deployment: ${relevant.join(', ')}"
    } else {
        echo "There are no changes that would affect the deployment"
    }

    def returnValues = [isPackageJsonChanged, hasRelevantChanges]
    echo "returnValues: ${returnValues}"
    return returnValues
}
