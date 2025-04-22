@Library('jenkins-shared-pipeline') _
pipeline {
    agent {
        label 'linux' // Ensure the agent is a Linux machine
    }
    stages {
        stage('Check Prerequisites') {
            steps {
                sh '''
                    echo "Checking system prerequisites..."
                    command -v curl >/dev/null 2>&1 || { echo "curl is required but not installed. Aborting."; exit 1; }
                    command -v unzip >/dev/null 2>&1 || { echo "unzip is required but not installed. Aborting."; exit 1; }
                    command -v wget >/dev/null 2>&1 || { echo "wget is required but not installed. Aborting."; exit 1; }
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