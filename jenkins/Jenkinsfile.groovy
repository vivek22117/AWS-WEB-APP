def createSGStack(String region, String stack, String vpc) {
    sh "aws cloudformation --region ${region} validate-template --template-body file://aws-infra-security-group.json"
    sh "aws cloudformation --region ${region} create-stack --stack-name ${stack} --template-body \
        file://aws-infra-security-group.json --parameters ParameterKey=VPCStackName,ParameterValue=${vpc}"
    sh "aws cloudformation --region ${region} wait stack-create-complete --stack-name ${stack}"
    sh "aws cloudformation --region ${region} describe-stack-events --stack-name ${stack} \
        --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
            --output table"
}

def createASGStack(String region, String stack) {
    sh "aws cloudformation --region ${region} validate-template --template-body file://aws-asg-for-ec2.json"
    sh "aws cloudformation --region ${region} create-stack --stack-name ${stack} --template-body \
        file://aws-asg-for-ec2.json --capabilities CAPABILITY_NAMED_IAM --parameters file://parameter/aws-asg-parameters.json"
    sh "aws cloudformation --region ${region} wait stack-create-complete --stack-name ${stack}"
    sh "aws cloudformation --region ${region} describe-stack-events --stack-name ${stack} \
        --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
        --output table"
}


pipeline {
    agent any

    options {
        timestamps()
    }
    parameters {
        string(name: 'VPC_NAME', defaultValue: 'vpc-subnet-network-by-vivek', description: 'Name of VPC Created')
        string(name: 'REGION', defaultValue: 'us-east-1', description: 'worspace to use in Terraform')
        string(name: 'SGSTACK', defaultValue: 'security-group-by-vivek', description: 'worspace to use in Terraform')
        string(name: 'ELBSTACK', defaultValue: 'elb-by-vivek', description: 'worspace to use in Terraform')
        string(name: 'ASGSTACK', defaultValue: 'asg-webapp-by-vivek', description: 'worspace to use in Terraform')
        string(name: 'DBSTACK', defaultValue: 'rds-by-vivek', description: 'RDS for Web Server')
    }
    stages {
        stage('web-app-security') {
            steps {
                dir('aws-cloudformation/security/') {
                    script {
                        def apply = true
                        def status = null
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                            --stack-name ${params.SGSTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            apply = true
                        } catch (err) {
                            apply = false
                            sh "echo Creating Security Group for Web-App"
                            createSGStack($ { params.REGION }, $ { params.SGSTACK }, $ { params.VPC_NAME })
                        }
                        if (apply) {
                            try {
                                sh "echo Stack exists, attempting update..."
                                sh "aws cloudformation --region ${params.REGION} update-stack --stack-name \
                                    ${params.SGSTACK} --template-body file://aws-infra-security-group.json \
                                    --parameters ParameterKey=VPCStackName,ParameterValue=${params.VPC_NAME}"
                            } catch (err) {
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('web-app-elb') {
            steps {
                dir('aws-cloudformation/loadbalancing/') {
                    script {
                        def apply = true
                        def status = null
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.ELBSTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            apply = true
                        } catch (err) {
                            apply = false
                            sh 'echo Creating ELB for web application....'
                            sh "aws cloudformation --region ${params.REGION} validate-template --template-body file://aws-elb-for-ec2.json"
                            sh "aws cloudformation --region ${params.REGION} create-stack --stack-name ${params.ELBSTACK} --template-body \
                                    file://aws-elb-for-ec2.json --parameters file://parameter/aws-elb-parameters.json"
                            sh "aws cloudformation --region ${params.REGION} wait stack-create-complete --stack-name ${params.ELBSTACK}"
                            sh "aws cloudformation --region ${params.REGION} describe-stack-events --stack-name ${params.ELBSTACK} \
                                    --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
                                    --output table"
                        }
                        if (apply) {
                            try {
                                sh "echo Stack exists, attempting update..."
                                sh "aws cloudformation --region ${params.REGION} update-stack --stack-name \
                                    ${params.ELBSTACK} --template-body file://aws-elb-for-ec2.json \
                                    --parameters file://parameter/aws-elb-parameters.json"
                            } catch (error) {
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('web-app-asg') {
            steps {
                dir('aws-cloudformation/elasticity/') {
                    script {
                        def apply = true
                        def status = null
                        def region = 'us-east-1'
                        def stackName = 'asg-webapp-by-vivek'
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.ASGSTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            sh "echo $status"
                            if (status == 'DELETE_FAILED' || 'ROLLBACK_COMPLETE' || 'ROLLBACK_FAILED' || 'UPDATE_ROLLBACK_FAILED') {
                                sh "aws cloudformation delete-stack --stack-name ${params.ASGSTACK} --region ${params.REGION}"
                                sh 'echo Creating ASG group and configuration for web application....'
                                createASGStack(region, stackName)
                            }
                            apply = true
                        } catch (err) {
                            apply = false
                            sh 'echo Creating ASG group and configuration for first time....'
                            createASGStack(region, stackName)
                        }
                        if (apply) {
                            try {
                                sh "echo Stack exists, attempting update..."
                                sh "aws cloudformation --region ${params.REGION} update-stack --stack-name \
                                    ${params.ASGSTACK} --template-body file://aws-asg-for-ec2.json \
                                    --parameters file://parameter/aws-asg-parameters.json"
                            } catch (err) {
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('rds-web-server') {
            steps {
                dir('aws-cloudformation/database/') {
                    script {
                        def apply = true
                        def status = null
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.DBSTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            apply = true
                        } catch (err) {
                            apply = false
                            sh 'echo Creating ASG group and configuration for web application....'
                            sh "aws cloudformation --region ${params.REGION} validate-template --template-body file://aws-rds-database.json"
                            sh "aws cloudformation --region ${params.REGION} create-stack --stack-name ${params.DBSTACK} --template-body \
                                file://aws-rds-database.json --capabilities CAPABILITY_NAMED_IAM --parameters file://parameter/aws-rds-parameters.json"
                            sh "aws cloudformation --region ${params.REGION} wait stack-create-complete --stack-name ${params.DBSTACK}"
                            sh "aws cloudformation --region ${params.REGION} describe-stack-events --stack-name ${params.DBSTACK} \
                                --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
                                --output table"
                        }
                        if (apply) {
                            try {
                                sh "echo Stack exists, attempting update..."
                                sh "aws cloudformation --region ${params.REGION} update-stack --stack-name \
                                    ${params.DBSTACK} --template-body file://aws-rds-database.json \
                                    --parameters file://parameter/aws-rds-parameters.json"
                            } catch (err) {
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
    }
}