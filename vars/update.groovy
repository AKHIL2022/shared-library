def call (String gitEnvDevBranchName, String gitEnvRepoCredentialsId, String gitEnvUrl, String gitEnvRepoName, String packageName, String s3BucketName, 
          String s3ObjectName, String versionFileName, String authorEmail, String authorName ) {
        withCredentials([sshUserPrivateKey(credentialsId: gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
          sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git clone --depth=1 --branch ${gitEnvDevBranchName} ${gitEnvUrl}"
        }
        script {
          def gitCommitHash = sh(
          script: "git log -n 1 --pretty=format:'%H'",
          returnStdout: true
        )
          def gitCommitDate = sh(
          script: "git log -n 1 --pretty=format:'%cI'",
          returnStdout: true
        )
          def gitCommitSubject = sh(
          script: "git log -n 1 --pretty=format:'%s'",
          returnStdout: true
        )
          gitCommitSubject = gitCommitSubject.replace('"', '\\"')
          def gitCommitAuthorName = sh(
          script: "git log -n 1 --pretty=format:'%aN'",
          returnStdout: true
        )
          def gitCommitAuthorEmail = sh(
          script: "git log -n 1 --pretty=format:'%aE'",
          returnStdout: true
        )
        }
        dir(gitEnvRepoName) {
          writeFile(
          file: versionFileName,
          text: """\
            # Module: ${packageName}
            locals {
              ${packageName}_commitHash = "${gitCommitHash}"
              ${packageName}_commitDate = "${gitCommitDate}"
              ${packageName}_bucketName = "${s3BucketName}"
              ${packageName}_objectName = "${s3ObjectName}"
            }
            """.stripIndent()
        )
          sh "git add ${versionFileName}"
          sh """\
          git -c \"user.name=${authorName}\" \
              -c \"user.email=${authorEmail}\" \
              commit -m \"${gitCommitSubject} (${packageName})\" \
              --author=\"${gitCommitAuthorName} <${gitCommitAuthorEmail}>\"
          """.stripIndent()
          retry(3) {
            withCredentials([sshUserPrivateKey(credentialsId: gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
              sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git -c \"user.name=${authorName}\" -c \"user.email=${authorEmail}\" pull --rebase origin refs/heads/${gitEnvDevBranchName}"
              sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git push origin HEAD:refs/heads/${gitEnvDevBranchName}"
            }
          }
        }
      }
