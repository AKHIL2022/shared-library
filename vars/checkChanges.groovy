        stage('Check Changes') {
            steps {
                script {
                    // Define the function within the pipeline
                    def checkChanges(String localFolderName) {
                        def changes = []
                        def build = currentBuild
                        def relevant
                        boolean isPackageJsonChanged
                        boolean hasRelevantChanges
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
                            element ==~ (localFolderName == "" ? /.*$/ : /\Q${localFolderName}\E\/.*/)
                        }
                        relevant = relevant.findAll { element ->
                            // Ignore changes to *.test.js files
                            !(element ==~ /.*\.test\.js/)
                        }
                        isPackageJsonChanged = relevant.any { element ->
                            element ==~ /.*package\.json/
                        }
                        hasRelevantChanges = !relevant.isEmpty()
                        if (hasRelevantChanges) {
                            echo "There are changes that affect the deployment: ${relevant.join(', ')}"
                        } else {
                            echo 'There are no changes that would affect the deployment'
                        }
                        return [isPackageJsonChanged, hasRelevantChanges]
                    }
                }
            }
        }
