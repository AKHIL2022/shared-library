def call(String localFolderName) {
    def changes = []
    def build = currentBuild
    def relevant = []
    def isPackageJsonChanged
    def hasRelevantChanges

    // Normalize localFolderName
    if (localFolderName == './' || localFolderName == '.') {
        localFolderName = ''
    }
    echo "Using localFolderName: '${localFolderName}'"

    // Collect changes since last successful build
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
            changes = ["${localFolderName == '' ? '' : localFolderName + '/'}package.json"]
        }
    }

    changes = changes.unique().sort()
    echo "Changes since last successful build: ${changes.isEmpty() ? 'none' : changes.join(', ')}"

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
