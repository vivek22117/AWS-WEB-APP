import javaposse.jobdsl.dsl.DslFactory

DslFactory factory = this
String contactEmail = "vivekmishra22117@gmail.com"

factory.job("vpc-network") {
    deliveryPipelineConfiguration("vpc")
    triggers { githubPush() }
    scm { github("vivek22117/jenkins-example") }

    wrappers { colorizeOutput() }

    steps {
        shell('''
#!/bin/bash

#validate the template
aws cloudformation validate-template --template-body file://aws-cloudformation/network/aws-vpc-subnet-network.json

#Execute this command if you want to UPDATE STACK
#aws cloudformation update-stack \
#--stack-name vpc-subnet-network-by-vivek \
#--template-body file://aws-cloudformation/network/aws-vpc-subnet-network.json

#create stack using template file
aws --profile yash cloudformation create-stack \
--stack-name vpc-subnet-network-by-vivek \
--template-body file://aws-cloudformation/network/aws-vpc-subnet-network.json \
--parameters ParameterKey=VpcCidrBlock,ParameterValue=10.0.0.0/22

#wait for stack to complete
aws --profile yash cloudformation wait stack-create-complete \
--stack-name vpc-subnet-network-by-vivek && echo 'Waiting for template to complete......\'

#DESCRIBE STACK CREATION EVENTS
aws --profile yash cloudformation describe-stack-events \
--stack-name vpc-subnet-network-by-vivek \
--query "StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]" \
--output table


#list the exports done during cloudformation
aws --profile yash cloudformation list-exports --query "Exports[].[Name, Value]" --output table

''')
    }

    publishers {
        mailer(contactEmail, false, true)
    }
}