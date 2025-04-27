def call(Map config) {
    pipeline {
        agent {
            docker {
                image 'node:16-bullseye' // Base image with Node.js 16
                args '-v $HOME/.m2:/root/.m2' // Maven cache persistence
            }
        }
        tools {
            maven 'Maven-3.8.6' // Name from Jenkins Global Tool Config
            jdk 'JDK17' // Name from Jenkins Global Tool Config
        }
        stages {
            stage('Clone Repository') {
                steps {
                    git url: config.repoUrl, branch: config.branch
                }
            }

            stage('Setup Environment') {
                steps {
                    script {
                        // Verify installations
                        sh """
                            node --version
                            npm --version
                            java --version
                            mvn --version
                        """
                        
                        // Install specific npm version if needed
                        sh 'npm install -g npm@8'
                    }
                }
            }

            stage('Install Dependencies') {
                steps {
                    sh 'npm install'
                    sh 'mvn dependency:resolve' // If using Maven
                }
            }

            stage('Build') {
                steps {
                    sh 'npm run build'
                }
            }
        }
        post {
            always {
                archiveArtifacts artifacts: 'build.log', allowEmptyArchive: true
            }
        }
    }
}
