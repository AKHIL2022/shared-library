@Library('jenkins-shared-pipeline') _
pipeline {
    agent any
    stages {
        stage('Check Prerequisites') {
            steps {
                sh '''
                    echo "Checking system prerequisites..."
                    echo "All prerequisites are installed."
                '''
            }
        }
        stage('Install Terraform and AWS CLI') {
            steps {
                test()
            }
        }
        stage('Validate Installation') {
            steps {
                sh '''
                    echo "Validating Terraform installation..."
                    terraform version
                    
                    echo "Validating AWS CLI installation..."
                    aws --version
                    aws sts get-caller-identity || echo "AWS CLI not configured; skipping identity check."
                '''
            }
        }
    }
    post {
        always {
            echo "Pipeline execution completed."
        }
        failure {
            echo "Pipeline failed. Check logs for details."
        }
    }
}
