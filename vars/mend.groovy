def call(Map config) {
    String productName = config.productName ?: 'MyHCLSoftware'
    String apiKeyCredentialId = config.apiKeyCredentialId ?: 'mend-api-key'
    String folderName = config.folderName ?: 'shared-library'
    
    dir(folderName) {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${config.projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                echo 'Running NPM Audit, Job will fail if there are high priority issues'
                if (isPackageJsonChanged) {
                    echo 'Downloading Mend Unified Agent'
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
                } 
            }
        }
    }
}
