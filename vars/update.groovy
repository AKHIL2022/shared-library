def call (String gitEnvRepoCredentialsId, String gitEnvDevBranchName, String gitEnvUrl, String gitEnvRepoName,
          String versionFileName, String packageName, String s3ObjectName, String applicationName) {
  String s3BucketName = 'tf-test-1'
  String authorName = 'Jenkins Build'
  String authorEmail = 'build@example.com'
  String gitEnvFolderName = "${applicationName}-deployment"
  String gitCommitHash, gitCommitDate, gitCommitSubject, gitCommitAuthorName, gitCommitAuthorEmail
  echo "${gitEnvFolderName}"
  echo "${authorName}"
  echo "${authorEmail}"
  echo "${s3BucketName}"        
          

  withCredentials([sshUserPrivateKey(credentialsId: gitEnvRepoCredentialsId, keyFileVariable: 'SSH_KEY')]) {
    sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" git clone --depth=1 --branch ${gitEnvDevBranchName} ${gitEnvUrl} ${gitEnvFolderName}"
  }
  script {
    gitCommitHash = sh(
          script: "git log -n 1 --pretty=format:'%H'",
          returnStdout: true
          ).trim()
    gitCommitDate = sh(
          script: "git log -n 1 --pretty=format:'%cI'",
          returnStdout: true
          ).trim()
    gitCommitSubject = sh(
          script: "git log -n 1 --pretty=format:'%s'",
          returnStdout: true
          ).trim()
    gitCommitSubject = gitCommitSubject.replace('"', '\\"')
    gitCommitAuthorName = sh(
          script: "git log -n 1 --pretty=format:'%aN'",
          returnStdout: true
          ).trim()
    gitCommitAuthorEmail = sh(
          script: "git log -n 1 --pretty=format:'%aE'",
          returnStdout: true
         ).trim()
  }
          echo "${gitCommitHash}"
          echo "${gitCommitDate}"
          echo "${gitCommitSubject}"
          echo "${gitCommitAuthorName}"
          echo "${gitCommitAuthorEmail}"
  dir(gitEnvFolderName) {
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
