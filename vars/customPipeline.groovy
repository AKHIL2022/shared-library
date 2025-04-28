def call(Map config) {
   pipeline {
    agent any
    stages {
        stage('Clone Repository') {
            steps {
                git url: config.repoUrl, branch: config.branch
            }
        }

        stage('Install AWS CLI') {
            steps {
                sh '''#!/bin/bash
                    # Update and install required dependencies
                    apt-get update && apt-get install -y \\
                        curl \\
                        unzip

                    # Install AWS CLI v2
                    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    unzip awscliv2.zip
                    ./aws/install
                '''
            }
        }

        stage('Install Terraform') {
            steps {
                sh '''#!/bin/bash
                    apt-get update && apt-get install -y \\
                        gnupg \\
                        software-properties-common \\
                        lsb-release

                    curl -fsSL https://apt.releases.hashicorp.com/gpg | gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
                    echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | tee /etc/apt/sources.list.d/hashicorp.list
                    
                    # Install Terraform
                    apt-get update && apt-get install -y terraform
                '''
            }
        }

        stage('Verify Installations') {
            steps {
                sh '''#!/bin/bash
                    echo "AWS CLI version:"
                    aws --version
                    echo "\\nTerraform version:"
                    terraform --version
                '''
            }
        }
    }
}
}
