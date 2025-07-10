def call(String gitEnvRepoCredentialsId, String gitEnvDevBranchName, String gitEnvUrl, 
         String versionFileName, String lamdaName, String s3ObjectName, String applicationName, String localFolderName, String localsFormat) {
  String s3BucketName = 'tf-test-1'
  String authorName = 'Jenkins Build'
  String authorEmail = 'build@example.com'
  String gitEnvFolderName = "floward-exercise-deployment-39"
  String gitCommitHash, gitCommitDate, gitCommitSubject, gitCommitAuthorName, gitCommitAuthorEmail
  echo "${gitEnvFolderName}"
  echo "${authorName}"
  echo "${authorEmail}"
  echo "${s3BucketName}"        

    withCredentials([sshUserPrivateKey(credentialsId: gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
        sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git clone --depth=1 --branch ${gitEnvDevBranchName} ${gitEnvUrl}"
    }
    script {
        gitRepo = sh(
            script: 'git ls-remote --get-url origin',
            returnStdout: true
        ).trim()
        gitCommitHash = sh(
            script: "git log -n 1 --pretty=format:'%h' -- ${localFolderName}",
            returnStdout: true
        ).trim()
        gitCommitDate = sh(
            script: "git log -n 1 --pretty=format:'%cI' -- ${localFolderName}",
            returnStdout: true
        ).trim()
        gitCommitSubject = sh(
            script: "git log -n 1 --pretty=format:'%s' -- ${localFolderName}",
            returnStdout: true
        ).trim()
        gitCommitSubject = gitCommitSubject.replace('"', '\\"')
        gitCommitAuthorName = sh(
            script: "git log -n 1 --pretty=format:'%aN' -- ${localFolderName}",
            returnStdout: true
        ).trim()
        gitCommitAuthorEmail = sh(
            script: "git log -n 1 --pretty=format:'%aE' -- ${localFolderName}",
            returnStdout: true
        ).trim()
    }
    dir(gitEnvFolderName) {
        def localsContent
        if (localsFormat == 'lamdaPrefixed') {
            localsContent = """\
              # Lambda: ${lamdaName}
              # Branch: ${GIT_BRANCH.replaceFirst('.+?/', '')}
              locals {
                ${lamdaName}_commitHash = "${gitCommitHash}"
                ${lamdaName}_commitDate = "${gitCommitDate}"
                ${lamdaName}_bucketName = "${s3BucketName}"
                ${lamdaName}_objectName = "${s3ObjectName}"
              }
              """.stripIndent()
        } else {
            localsContent = """\
              # Lambda: ${lamdaName}
              # Branch: ${GIT_BRANCH.replaceFirst('.+?/', '')}
              locals {
                function_src_commit_url = "${gitRepo}/${localFolderName}"
                function_src_commit_hash = "${gitCommitHash}"
                function_src_commit_date = "${gitCommitDate}"
                function_src_bucket_name = "${s3BucketName}"
                function_src_object_name = "${s3ObjectName}"
              }
              """.stripIndent()
        }
        writeFile(
            file: versionFileName,
            text: localsContent
        )
        sh "git add ${versionFileName}"
        sh """\
            git -c \"user.name=${authorName}\" \
                -c \"user.email=${authorEmail}\" \
                commit -m \"${gitCommitSubject} (${lamdaName})\" \
                --author=\"${gitCommitAuthorName} <${gitCommitAuthorEmail}>\"
            """.stripIndent()
        retry(3) {
            withCredentials([sshUserPrivateKey(credentialsId: gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
                sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git -c \"user.name=${authorName}\" -c \"user.email=${authorEmail}\" pull --rebase origin refs/heads/${gitEnvDevBranchName}"
                sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git push origin HEAD:refs/heads/${gitEnvDevBranchName}"
            }
        }
        deleteDir()
    }
}
