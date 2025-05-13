def call(Map params) {
    String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
    echo "- Application: ${params.projectName}"
    echo "- Directory: ${params.localFolderName}"
    echo "- Package.json Changed: ${params.IsPackageJsonChanged}"
    def mendScan = {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${params.projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                echo 'Running NPM Audit, Job will fail if there are high priority issues'
                if (params.IsPackageJsonChanged?.toBoolean() ?: true) {
                    echo 'Downloading Mend Unified Agent'
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
                }else {
                    echo 'Skipping Mend scan Package.json not changed'
                }
            }
        }
    }
    if (params.localFolderName ) {
        dir(params.localFolderName ) {
            mendScan() 
        }
    } else {
        mendScan()
    }
}
