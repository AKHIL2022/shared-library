def call() {
    echo "Installing Terraform and AWS CLI on Linux"
    
   echo 'Installing AWS CLI...'
                    sh '''
                        echo "Installing AWS CLI..."
                        curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                        unzip -o awscliv2.zip
                       ./aws/install --bin-dir $HOME/bin --install-dir $HOME/aws-cli --update
                        rm -rf awscliv2.zip aws
                    '''    
                    sh '''
                         echo "Installing Terraform..."
                         wget https://releases.hashicorp.com/terraform/1.5.0/terraform_1.5.0_linux_amd64.zip
                         unzip -o terraform_1.5.0_linux_amd64.zip -d $HOME/bin/
                         rm terraform_1.5.0_linux_amd64.zip
                    '''
    
    echo "Terraform and AWS CLI installation complete!"
}
