   def call(String projectName, isPackageJsonChanged, continueOnAuditFail) {
    boolean isPackageJsonChangedBool = isPackageJsonChanged.toBoolean()
    boolean continueOnAuditFailBool = continueOnAuditFail.toBoolean()
    String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                def criticalVul = sh(script: 'npm audit --audit-level=critical', returnStatus: true)
                def highVul = sh(script: 'npm audit --audit-level=high', returnStatus: true)
                if (criticalVul != 0) {
                    echo "npm audit found issues (exit code: ${criticalVul})"
                    if (continueOnAuditFailBool == false) {
                        echo "${continueOnAuditFail}"
                        error('Failing pipeline due to audit errors.')
                    } else {
                        echo "${continueOnAuditFailBool}"
                        unstable("Proceeding despite critical audit issues.")
                    }
                }
                if (highVul != 0) {
                    unstable('Proceeding despite audit issues.')
                }
                if (isPackageJsonChangedBool) {
                    echo 'Downloading Mend Unified Agent'
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
                } else {
                    echo 'Skipping Mend scan as Package.json is not changed'
                }
            }
        }
}
