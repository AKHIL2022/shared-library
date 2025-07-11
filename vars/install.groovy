def call(String gitCredentialId) {
    withCredentials([sshUserPrivateKey(credentialsId: gitCredentialId, keyFileVariable: 'SSH_KEY')]) {
        sh "GIT_SSH_COMMAND=\"ssh -i \\\"$SSH_KEY\\\"\" npm ci"
    }
}
