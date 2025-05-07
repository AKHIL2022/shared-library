def call() {
stage('Security Audit') {
        environment {
        WS_APIKEY = credentials('mend-api-key')
        WS_WSS_URL = 'https://saas.whitesourcesoftware.com/agent'
        WS_PRODUCTNAME = 'HCLCODE'
        WS_PROJECTNAME = "${applicationName}"
      }
      steps {
        dir(localFolderName) {
          script {
            if (IsPackageJsonChanged) {
              script {
              echo 'Downloading Mend Unified Agent'
              sh 'curl -LJO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar'
              echo 'Generate Mend Report'
              sh 'java -jar wss-unified-agent.jar'
              }
            }
          }
        }
      }
    }
}
