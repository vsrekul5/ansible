pipeline{
    agent any
    stages{
        stage('Build'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/vsrekul5/python-app.git']]])                                
            }
        }
        stage('create image'){
            steps{
                script{
                    sh 'docker build -t vsrekul/ansibleapp .'
                }                
            }                
        }
        stage('upload the image to the Dockr Hub'){
            steps{
                script{
                    docker.withRegistry('', registryCredentials){
                    sh 'docker push vsrekul/ansibleapp'
                    }                
                } 
            }
        }
        stage('stop container'){
            steps{
                script{
                    sh 'docker ps -f name=pyappeployContainer -q | xargs --no-run-if-empty docker container stop'
                    sh 'docker container ls -a -fname=pyappeployContainer -q | xargs -r docker container rm'
                }                
               
            }
        }
        stage('Run the app in a docker container'){
            steps{
                script{
                    dockerImage.run("-p 8096:5000 --rm --name pyappeployContainer")
                }
                
            }
        }
    }  
}