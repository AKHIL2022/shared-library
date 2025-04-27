def call(Map config) {
    pipeline {
        agent any
        stages {
            stage('Clone Repository') {
                steps {
                    script {
                        git url: config.repoUrl, branch: config.branch
                        echo "Cloned ${config.repoUrl} (${config.branch})"
                    }
                }
            }

            stage('Setup Dependencies') {
                steps {
                    script {
                        if(fileExists('package.json')) {
                            sh 'npm install'
                            echo "Node modules installed"
                        } else {
                            echo "No package.json found - skipping npm install"
                        }
                    }
                }
            }

            stage('Run Basic Checks') {
                steps {
                    script {
                        sh 'node --version || echo "Node not installed"'
                        sh 'npm --version || echo "NPM not installed"'
                        sh 'java -version'
                    }
                }
            }

            stage('Build') {
                steps {
                    sh 'echo "Simulating build process" > build.log'
                    sh 'cat build.log'
                }
            }
        }
        post {
            always {
                echo "Pipeline complete - ${currentBuild.result ?: 'SUCCESS'}"
                archiveArtifacts artifacts: 'build.log', allowEmptyArchive: true
            }
        }
    }
}
