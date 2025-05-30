def call(String projectName, isPackageJsonChanged, force_buid) {
    String productName = 'HCLCODE'
    String apiKeyCredentialId = apiKeyCredentialId ?: 'mend-api-key'
    echo "isPackageJsonChanged: ${isPackageJsonChanged}"
    echo "force_build: ${force_build}"
        withEnv([
            "WS_PRODUCTNAME=${productName}",
            "WS_PROJECTNAME=${projectName}",
            "WS_WSS_URL=https://saas.whitesourcesoftware.com/agent"
        ]) {
            withCredentials([string(credentialsId: apiKeyCredentialId, variable: 'WS_APIKEY')]) {
                    if (!force_build) {
                        error('Failing pipeline due to audit errors.')
                    } else {
                        unstable("Proceeding despite audit issues")
                    }
                }
                if (isPackageJsonChanged) {
                    echo 'Downloading Mend Unified Agent'
                    sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
                    echo 'Generate Mend Report'
                    sh 'java -jar wss-unified-agent.jar'
                } else {
                    unstable("Skipping Mend scan as Package.json is not changed")
                }
            }
        }
