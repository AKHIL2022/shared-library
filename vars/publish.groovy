def call(String applicationName, String componentName, String bundleFileName, String s3BucketName ) {
  echo "applicationName: ${applicationName}"
  echo "packageName: ${packageName}"
  echo "bundleFileName: ${bundleFileName}"
  echo "s3BucketName: ${s3BucketName}"
  def s3ObjectName
        script {
          def version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
          s3ObjectName = "${applicationName}/${componentName}/${version}.zip"
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
      return s3ObjectName
}
