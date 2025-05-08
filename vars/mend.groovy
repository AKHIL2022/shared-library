def call(Map params) {
    String productName = 'MyHCLSoftware'
    String apiKeyCredentialId = params.apiKeyCredentialId ?: 'mend-api-key'
    
    dir(params.localFolderName) {
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${params.applicationName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                echo 'Running NPM Audit, Job will fail if there are high priority issues'
                echo "- WS_WSS_URL    : ${env.WS_WSS_URL}"
                echo "- WS_PRODUCTNAME: ${env.WS_PRODUCTNAME}"
                echo "- WS_PROJECTNAME: ${env.WS_PROJECTNAME}"
                echo "- WS_KEY: ${env.WS_APIKEY}"
                echo "-folderName: ${env.localFolderName}"
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
