def call() {
      script {
        version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
        s3ObjectName = "${applicationName}/${packageName}/${version}.zip"
      }
    withAWS(region: 'us-east-1', credentials: 'aws-deployment-backend') {
        s3Upload(
            pathStyleAccessEnabled: true,
            payloadSigningEnabled: true,
            file: bundleFileName,
            bucket: s3BucketName,
            path: s3ObjectName
        )
    }
}
