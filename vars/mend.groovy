   def call(String projectName, isPackageJsonChanged , forceBuild) {
    String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
      
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                    if (!forceBuild) {
                        error('Failing pipeline due to audit errors.')
                    } else {
                        unstable("Proceeding despite audit issues.")
                    }
                if (isPackageJsonChanged) {
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
