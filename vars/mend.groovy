def call(String projectName,String localFolderName,Boolean isPackageJsonChanged) {
    String productName = 'HCLCODE'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
    
    dir(params.localFolderName) {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                echo 'Running NPM Audit, Job will fail if there are high priority issues'
                echo "=== Mend Configuration ==="
                echo "- Application: ${params.applicationName}"
                echo "- Directory: ${params.localFolderName}"
                echo "- Package.json Changed: ${params.isPackageJsonChanged}"
                if (params.isPackageJsonChanged) {
                    echo 'Downloading Mend Unified Agent'
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
                } 
            }
        }
    }
}
