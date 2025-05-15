def call(Map params) {
      if (!binding.hasVariable('applicationName'))
      if (!binding.hasVariable('packageName'))
      if (!binding.hasVariable('bundleFileName'))
      if (!binding.hasVariable('s3BucketName'))
      if (!binding.hasVariable('s3ObjectName'))
      
      script {
        version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
        s3ObjectName = "${applicationName}/${packageName}/${version}.zip"
      }
    withAWS(region: 'us-east-1', credentials: 'aws-deployment-backend') {
        echo "Version is: ${version}"  
        echo "S3 Object Path: ${s3ObjectName}"
        s3Upload(
            pathStyleAccessEnabled: true,
            payloadSigningEnabled: true,
            file: bundleFileName,
            bucket: s3BucketName,
            path: s3ObjectName
        )
    }
}
