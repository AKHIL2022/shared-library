def call(Map params) {
  def s3ObjectName
  if (params.hasRelevantChanges == null || params.hasRelevantChanges || params.force_build) {
    def uploadToS3 = { String componentName
        script {
          def version = sh(script: "echo -n v\$(date +%Y%m%d-%H%M%S)", returnStdout: true)
          s3ObjectName = "${params.applicationName}/${componentName}/${version}.zip"
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
   echo "params.localFolderName"
   echo "params.Lambdaname"
   echo componentName
   if (params.localFolderName) {
        dir(params.localFolderName) {
            if (params.Lambdaname) {
                uploadToS3(params.Lambdaname)
            }
            if (params.packageName) {
                uploadToS3(params.packageName)
            }
        }
    } else {
        if (params.Lambdaname) {
            uploadToS3(params.Lambdaname)
        }
        if (params.packageName) {
            uploadToS3(params.packageName)
        }
    }
  }
}
