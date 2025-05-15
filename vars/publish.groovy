def call(Map params) {
      script {
        version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
        s3ObjectName = "${params.applicationName}/${params.packageName}/${version}.zip"
      }
    withAWS(region: 'us-east-1', credentials: 'aws-deployment-backend') {
        echo "Version is: ${version}"  
        echo "S3 Object Path: ${s3ObjectName}"
        echo "S3 bucketname: ${params.s3BucketName}"
        echo "S3 Object Path: ${s3ObjectName}"
        s3Upload(
            pathStyleAccessEnabled: true,
            payloadSigningEnabled: true,
            file: params.bundleFileName,
            bucket: params.s3BucketName,
            path: s3ObjectName
        )
    }
}
