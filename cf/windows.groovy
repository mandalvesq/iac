#!/usr/bin/env groovy

pipeline{
agent { label ''}
    stages {
        stage ('Approve') {
            steps{
                timeout(time: 1, unit: 'HOURS') {
                    input 'Deploy to Production?'
                }
            }
        }
        stage ('Git Clone') {
            steps{checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: '']]])}
                
        }   
    
        stage ('Nugget') {
            steps{bat '"C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Community\\MSBuild\\Microsoft\\NuGet\\16.0\\nuget.exe" restore  "C:\\jenkins\\workspace\\Pipeline\\API\\**.sln"'}
            
        }
        stage('Restore'){
            steps{bat '"C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Community\\MSBuild\\Current\\Bin\\MSBuild.exe" /t:Restore "C:\\jenkins\\workspace\\Pipeline\\API\\**.sln"'}
            

        }
        stage('Build'){
            steps{bat '"C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Community\\MSBuild\\Current\\Bin\\MSBuild.exe" /t:Build /p:Configuration=Debug /p:DeployOnBuild=true "C:\\jenkins\\workspace\\Pipeline\\API\\**.sln"'}       
        }

        stage('Compress'){
            steps{powershell 'Compress-Archive -Path C:\\jenkins\\workspace\\Pipeline\\API\\EJME.CrewManagement.Api\\bin\\Debug\\net461\\publish\\* -CompressionLevel Fastest -DestinationPath C:\\jenkins\\workspace\\Pipeline\\publish.zip'}       
        }  
    }   
    post { 
        always { 
            cleanWs()
        }
    }
}
