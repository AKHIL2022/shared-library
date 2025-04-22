def call() {
    echo "Installing Terraform and AWS CLI on Linux"
    
    sh '''
        # Install AWS CLI
        echo "Installing AWS CLI..."
        curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
        unzip -q awscliv2.zip
        ./aws/install
        aws --version
        
        # Install Terraform
        echo "Installing Terraform..."
        TERRAFORM_VERSION="1.5.7"
        wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip
        unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip
        mv terraform /usr/local/bin/
        terraform --version
        
        # Clean up
        rm -rf aws awscliv2.zip terraform_${TERRAFORM_VERSION}_linux_amd64.zip
    '''
    
    echo "Terraform and AWS CLI installation complete!"
}
