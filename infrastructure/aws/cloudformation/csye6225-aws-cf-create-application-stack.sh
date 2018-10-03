STACK_NAME=$1
VPC_NAME="network-csye6225-vpc"
EC2_NAME="${STACK_NAME}-csye6225-ec2"

# export vpcId=$(aws ec2 describe-vpcs --query "Vpcs[*].[CidrBlock,VpcId]" --output text|grep 10.0.0.0/16|awk '{print $2}')
vpcId="vpc-0ab3a3db9c26e76c4"
echo ${vpcId}
# export subnetId1=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone]' --output text|grep $vpcId|grep us-east-1a|awk '{print $1}')
# echo ${subnetId1}
# export subnetId2=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone]' --output text|grep $vpcId|grep us-east-1b|awk '{print $1}')
# export subnetId3=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone]' --output text|grep $vpcId|grep us-east-1c|awk '{print $1}')

# export eC2SecurityGroupId=$(aws ec2 describe-security-groups --query 'SecurityGroups[*].[VpcId, Description, GroupId]' --output text|grep $vpcId|grep webapp|awk '{print $3}')
eC2SecurityGroupId="sg-0ab7c20fd9ee55dc5"
subnetId1="subnet-0a883487b93e0b278"
# export rDSSecurityGroupId=$(aws ec2 describe-security-groups --query 'SecurityGroups[*].[VpcId, Description, GroupId]' --output text|grep $vpcId|grep rds|awk '{print $3}')
# export eC2RoleName=$(aws iam list-roles --query 'Roles[*].[RoleName]' --output text|grep EC2Service|awk '{print $1}')
echo ${eC2SecurityGroupId}
echo ${subnetId1}
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=VpcId,ParameterValue=$vpcId ParameterKey=EC2Name,ParameterValue=$EC2_Name ParameterKey=EC2SecurityGroup,ParameterValue=$eC2SecurityGroupId ParameterKey=SubnetId1,ParameterValue=$subnetId1


# aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters 
# ParameterKey=EC2Name,ParameterValue=$EC2_Name 
# ParameterKey=EC2SecurityGroup,ParameterValue=$eC2SecurityGroupId 
# ParameterKey=SubnetId1,ParameterValue=$subnetId1

# aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=VPCName,ParameterValue=$VPC_NAME ParameterKey=EC2Name,ParameterValue=$EC2_NAME
export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"