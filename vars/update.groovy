def call (Map params) {
        withCredentials([sshUserPrivateKey(credentialsId: params.gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
          sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git clone --depth=1 --branch ${params.gitEnvDevBranchName} ${params.gitEnvUrl}"
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
          def gitCommitSubject = gitCommitSubject.replace('"', '\\"')
          def gitCommitAuthorName = sh(
          script: "git log -n 1 --pretty=format:'%aN'",
          returnStdout: true
        )
          def gitCommitAuthorEmail = sh(
          script: "git log -n 1 --pretty=format:'%aE'",
          returnStdout: true
        )
        }
        dir(params.gitEnvRepoName) {
          writeFile(
          file: params.versionFileName,
          text: """\
            # Module: ${params.packageName}
            locals {
              ${params.packageName}_commitHash = "${gitCommitHash}"
              ${params.packageName}_commitDate = "${gitCommitDate}"
              ${params.packageName}_bucketName = "${params.s3BucketName}"
              ${params.packageName}_objectName = "${params.s3ObjectName}"
            }
            """.stripIndent()
        )
          sh "git add ${params.versionFileName}"
          sh """\
          git -c \"user.name=${params.authorName}\" \
              -c \"user.email=${params.authorEmail}\" \
              commit -m \"${gitCommitSubject} (${params.packageName})\" \
              --author=\"${gitCommitAuthorName} <${gitCommitAuthorEmail}>\"
          """.stripIndent()
          retry(3) {
            withCredentials([sshUserPrivateKey(credentialsId: params.gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
              sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git -c \"user.name=${params.authorName}\" -c \"user.email=${params.authorEmail}\" pull --rebase origin refs/heads/${params.gitEnvDevBranchName}"
              sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git push origin HEAD:refs/heads/${params.gitEnvDevBranchName}"
            }
          }
        }
      }
