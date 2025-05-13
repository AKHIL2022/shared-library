def call(Map params) {
    String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
    
    def mendScan = {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${params.applicationName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                echo 'Running NPM Audit, Job will fail if there are high priority issues'
                if (params.IsPackageJsonChanged?.toString()?.toBoolean() ?: true) {
                    echo 'Downloading Mend Unified Agent'
                     echo "=== Mend Configuration ==="
                     echo "- Application: ${params.projectName}"
                     echo "- Directory: ${params.folderName}"
                     echo "- Package.json Changed: ${params.IsPackageJsonChanged }"
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
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
