def call(Map params) {
        String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'

    def mendScan = {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${params.projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                    if (!params.force_build) {
                        error('Failing pipeline due to audit errors.')
                    } else {
                        unstable("Proceeding despite audit issues")
                    }
                }
                if (params.IsPackageJsonChanged == null || params.IsPackageJsonChanged) {
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
    if (params.localFolderName) {
        dir(params.localFolderName) {
            mendScan()
        }
    } else {
        mendScan()
    }
}
