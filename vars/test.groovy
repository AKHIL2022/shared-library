def call() {
    echo "Installing Terraform and AWS CLI on Linux"
    
   echo 'Installing AWS CLI...'
                    sh '''
                         echo "Installing AWS CLI..."
                         mkdir -p $HOME/bin
                         curl -o awscliv2.zip "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip"
                         unzip -o awscliv2.zip
                         ./aws/install --bin-dir $HOME/.local/bin --install-dir $HOME/.local/aws-cli --update
                         rm -rf awscliv2.zip aws
                         export PATH=$HOME/.local/bin:$PATH
                         echo "PATH is set to: $PATH"
                         echo "Validating AWS CLI installation..."
                         $HOME/.local/bin/aws --version
                    '''    
                    sh '''
                          echo "Installing Terraform..."
                          curl -o terraform_1.5.0_linux_amd64.zip https://releases.hashicorp.com/terraform/1.5.0/terraform_1.5.0_linux_amd64.zip
                          unzip -o terraform_1.5.0_linux_amd64.zip -d $HOME/bin/
                          rm terraform_1.5.0_linux_amd64.zip
                          export PATH=$HOME/bin:$PATH
                          echo "PATH is set to: $PATH"
                          echo "Validating Terraform installation..."
                          $HOME/bin/terraform version
                    '''
    
    echo "Terraform and AWS CLI installation complete!"
}
