def call(Map params) {
  def s3ObjectName
  def Lambdaname = params.get('Lambdaname')
  def packagename = params.get('packagename')
  def componentName = Lambdaname ?: packagename
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
   echo "${params.packagename}"
   echo "${params.Lambdaname}"
    echo "${componentName}"
  
    if (params.localFolderName) {
        dir(params.localFolderName) {
            uploadToS3()
        }
    } else {
        uploadToS3()
    }
  }
}
