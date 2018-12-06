STACK_NAME=$1
VPC_NAME="${STACK_NAME}-csye6225-vpc"
EC2Name="csye6225-ec2"
EC2VOL_SIZE="16"
EC2VOL_TYPE="gp2"
AMI_IMAGE="ami-9887c6e7"
DYNAMO_TABLE="csye6225"
MASTER_USERNAME="csye6225master"
MASTER_USERPWD="csye6225password"
DB_NAME="csye6225"
DB_INSTANCE_CLASS="db.t2.medium"
DB_INSTANCE_IDENTIFIER="csye6225-fall2018"
DB_ENGINE="MySQL"
SNSTOPICNAME="ForgotPassword"
COOLDOWN=60
MIN_SIZE=3
MAX_SIZE=10
DESIRED_CAPACITY=3
LAUNCH_CONFIGURATION_NAME="asg_launch_config"
EC2_TYPE="t2.micro"
ASSOCIATE_PUBLIC_IPADDRESS="true"
CODEDEPLOYAPPLICATIONNAME="CodeDeployApplication"

DOMAINNAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
DNS_NAME=${DOMAINNAME::-1}
echo $DNS_NAME
DNS=${DOMAINNAME#csye6225-fall2018-}
echo $DNS

BUCKET_NAME="${DNS}csye6225.com"
echo $BUCKET_NAME

export vpcId=$(aws ec2 describe-vpcs --query "Vpcs[*].[CidrBlock, VpcId]" --output text|grep 10.0.0.0/16|awk '{print $2}')
echo "VpcId : ${vpcId}"
export subnetId1=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.1.0/24|grep us-east-1a|awk '{print $1}')
echo "subnetId1 : ${subnetId1}"
export subnetId2=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.2.0/24|grep us-east-1b|awk '{print $1}')
echo "subnetId2 : ${subnetId2}"
export subnetId3=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.3.0/24|grep us-east-1c|awk '{print $1}')
echo "subnetId3 : ${subnetId3}"
# export eC2SecurityGroupId=$(aws ec2 describe-security-groups --query 'SecurityGroups[*].[VpcId, GroupName, GroupId]' --output text|grep ${vpcId}|grep csye6225-webapp|awk '{print $3}')
# echo "eC2SecurityGroupId : ${eC2SecurityGroupId}"
# export rDSSecurityGroupId=$(aws ec2 describe-security-groups --query 'SecurityGroups[*].[GroupName, GroupId]' --output text|grep rds|awk '{print $2}')
# echo "rDSSecurityGroupId : ${rDSSecurityGroupId}"
export eC2RoleName=$(aws iam list-roles --query 'Roles[*].[RoleName]' --output text|grep EC2Service|awk '{print $1}')
echo "eC2RoleName : ${eC2RoleName}"
export snsTopicArn=$(aws sns list-topics --output text | grep ${SNSTOPICNAME} | awk '{print $2}')
echo "snsTopicArn : ${snsTopicArn}"
export lambdaRoleArn=$(aws iam list-roles --query 'Roles[*].[RoleName,Arn]' --output text | grep Lambda |awk '{print $2}')
echo "lambdaRoleArn : ${lambdaRoleArn}"
export certificate_ARN=$(aws acm list-certificates --query CertificateSummaryList[0].CertificateArn --output text)
echo "certificateArn : ${certificate_ARN}"
export codeDeployServiceRoleArn=$(aws iam get-role --role-name CodeDeploySerivceRole --query "Role.Arn" --output text)
echo "codeDeployServiceRoleArn  : ${codeDeployServiceRoleArn}"

aws cloudformation create-stack --stack-name $STACK_NAME --capabilities "CAPABILITY_NAMED_IAM" --template-body file://csye6225-cf-auto-scaling-application.json --parameters ParameterKey=VpcId,ParameterValue=$vpcId ParameterKey=EC2Name,ParameterValue=$EC2Name ParameterKey=SubnetId1,ParameterValue=$subnetId1 ParameterKey=EC2VolumeSize,ParameterValue=$EC2VOL_SIZE ParameterKey=EC2VolumeType,ParameterValue=$EC2VOL_TYPE ParameterKey=AMIImage,ParameterValue=$AMI_IMAGE ParameterKey=DynamoDBName,ParameterValue=$DYNAMO_TABLE ParameterKey=MasterUsername,ParameterValue=$MASTER_USERNAME ParameterKey=MasterUserPwd,ParameterValue=$MASTER_USERPWD ParameterKey=DBName,ParameterValue=$DB_NAME ParameterKey=DBInstanceClass,ParameterValue=$DB_INSTANCE_CLASS ParameterKey=DBInstanceIdentifier,ParameterValue=$DB_INSTANCE_IDENTIFIER ParameterKey=DBEngine,ParameterValue=$DB_ENGINE ParameterKey=SubnetId2,ParameterValue=$subnetId2 ParameterKey=SubnetId3,ParameterValue=$subnetId3 ParameterKey=BucketName,ParameterValue=$BUCKET_NAME ParameterKey=EC2RoleName,ParameterValue=$eC2RoleName ParameterKey=SNSTopicArn,ParameterValue=$snsTopicArn ParameterKey=LambdaRoleArn,ParameterValue=$lambdaRoleArn ParameterKey=AssociatePublicIpAddress,ParameterValue=$ASSOCIATE_PUBLIC_IPADDRESS ParameterKey=Cooldown,ParameterValue=$COOLDOWN ParameterKey=MinSize,ParameterValue=$MIN_SIZE ParameterKey=MaxSize,ParameterValue=$MAX_SIZE ParameterKey=DesiredCapacity,ParameterValue=$DESIRED_CAPACITY ParameterKey=LaunchConfigurationName,ParameterValue=$LAUNCH_CONFIGURATION_NAME ParameterKey=EC2InstanceType,ParameterValue=$EC2_TYPE ParameterKey=CertificateArn,ParameterValue=$certificate_ARN ParameterKey=CodeDeployServiceRoleArn,ParameterValue=$codeDeployServiceRoleArn ParameterKey=DNS,ParameterValue=$DNS_NAME ParameterKey=CodeDeployApplicationName,ParameterValue=$CODEDEPLOYAPPLICATIONNAME

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
if [ $STACK_STATUS = "CREATE_COMPLETE" ]
then
	echo "Created Stack ${STACK_NAME} successfully!"
else
	echo "Problem in Stack Creation. Try again!"
fi
