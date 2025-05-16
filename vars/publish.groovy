def call(Map params) {
      script {
        def version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
        def s3ObjectName = "${params.applicationName}/${params.packageName}/${version}.zip"
      }
    withAWS(region: 'us-east-1', credentials: 'aws-deployment-backend') {
        s3Upload(
            pathStyleAccessEnabled: true,
            payloadSigningEnabled: true,
            file: params.bundleFileName,
            bucket: params.s3BucketName,
            path: s3ObjectName
        )
    }
    return s3ObjectName  
}
