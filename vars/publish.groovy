def call(Map params) {
  def s3ObjectName
  def lambdaname = params.get('Lambdaname')
  def packagename = params.get('packageName')
  def componentName = Lambdaname ?: packageName
  if (params.hasRelevantChanges == null || params.hasRelevantChanges || params.force_build) {
    def uploadToS3 = {
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
   echo "${params.packageName}"
   echo "${params.Lambdaname}"
  
    if (params.localFolderName) {
        dir(params.localFolderName) {
            uploadToS3(componentName)
        }
    } else {
        uploadToS3(componentName)
    }
  }
}
