def call(String localFolderName) {
    echo "${localFolderName}"
    def changes = []
    def build = currentBuild
    def relevant = []
    def isPackageJsonChanged
    def hasRelevantChanges

   // Collect changes only from the current build
    if (build.changeSets.isEmpty()) {
        echo "No changes detected in current build ${build.id}"
    } else {
        for (changeLog in build.changeSets) {
            for (entry in changeLog.items) {
                for (file in entry.affectedFiles) {
                    changes += file.path
                }
            }
        }
    }

    // Fallback for no changes or no prior successful build
    if (changes.isEmpty() && !build.previousBuild) {
        echo "No prior builds and no changes, assuming package.json changed"
        changes = ["${localFolderName == '' ? '' : localFolderName + '/'}package.json"]
    }

    changes = changes.unique().sort()
    echo "Changes in current build: ${changes.isEmpty() ? 'none' : changes.join(', ')}"

    // Filter relevant changes
    if (localFolderName == '') {
        relevant = changes.findAll { !(it ==~ /.*\.test\.js/) }
    } else {
        relevant = changes.findAll { it ==~ /\Q${localFolderName}\E\/.*/ && !(it ==~ /.*\.test\.js/) }
    }

    echo "Relevant changes: ${relevant.isEmpty() ? 'none' : relevant.join(', ')}"

    // Check for package.json
    isPackageJsonChanged = relevant.any { it ==~ /.*\/?package\.json/ }
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
