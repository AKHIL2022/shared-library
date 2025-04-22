def call() {
    echo "Installing Terraform and AWS CLI on Linux"
    
   echo 'Installing AWS CLI...'
                    sh '''
                        # Download and unzip AWS CLI
                        curl -s -o awscliv2.zip https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip
                        unzip -o awscliv2.zip
                        
                        # Install and verify
                        ./aws/install --bin-dir /usr/local/bin --install-dir /usr/local/aws-cli --update
                        aws --version
                    '''
                    
                    // Install Terraform
                    echo 'Installing Terraform...'
                    sh '''
                        TERRAFORM_VERSION="1.5.7"
                        # Download and unzip Terraform
                        wget -q https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip
                        unzip -o terraform_${TERRAFORM_VERSION}_linux_amd64.zip
                        
                        # Install and verify
                        mv terraform /usr/local/bin/
                        terraform --version
                    '''
    
    echo "Terraform and AWS CLI installation complete!"
}
