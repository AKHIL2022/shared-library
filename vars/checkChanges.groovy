def call(String localFolderName) {
    def changes = []
    def build = currentBuild
    def relevant = []
    def isPackageJsonChanged
    def hasRelevantChanges

    // Normalize localFolderName
    if (localFolderName == './' || localFolderName == '.') {
        localFolderName = ''
        echo "localFolderName normalized to empty string for repository root"
    } else {
        echo "localFolderName set to: ${localFolderName}"
    }

    // Collect changes since last successful build
    while (build != null && build.result != 'SUCCESS') {
        if (build.changeSets.isEmpty()) {
            echo "Warning: No changes detected in build ${build.id}"
        }
        for (changeLog in build.changeSets) {
            for (entry in changeLog.items) {
                for (file in entry.affectedFiles) {
                    changes += file.path
                }
            }
        }
        build = build.previousBuild
        if (!build) {
            echo "No prior builds found, assuming package.json changed"
            changes = ["${localFolderName == '' ? '' : localFolderName + '/'}package.json"]
        }
    }

    echo "Raw changes: ${changes}"
    changes = changes.unique().sort()
    echo "Unique sorted changes: ${changes.isEmpty() ? 'none' : changes.join(', \n')}"

    // Debug filtering step-by-step
    echo "Applying folder filter with localFolderName: '${localFolderName}'"
    def folderFiltered = changes.findAll { element ->
        def include = localFolderName == '' ? true : element ==~ /\Q${localFolderName}\E\/.*/ 
        echo "Checking file: ${element}, Include: ${include}"
        return include
    }
    echo "After folder filter: ${folderFiltered.isEmpty() ? 'none' : folderFiltered.join(', \n')}"

    // Apply test file exclusion
    relevant = folderFiltered.findAll { element ->
        def isTestFile = element ==~ /.*\.test\.js/
        echo "Checking file: ${element}, Is test file: ${isTestFile}"
        return !isTestFile
    }

    echo "Relevant changes: ${relevant.isEmpty() ? 'none' : relevant.join(', \n')}"

    // Check for package.json
    isPackageJsonChanged = relevant.any { element ->
        def isPackageJson = element ==~ /.*\/?package\.json/
        echo "Checking for package.json: ${element}, Matches: ${isPackageJson}"
        return isPackageJson
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
