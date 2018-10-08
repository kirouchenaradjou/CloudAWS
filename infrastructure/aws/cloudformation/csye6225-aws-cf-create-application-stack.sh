STACK_NAME=$1
VPC_NAME="${STACK_NAME}-csye6225-vpc"
EC2_NAME="${STACK_NAME}-csye6225-ec2"
EC2VOL_SIZE="16"
EC2VOL_TYPE="gp2"
AMI_IMAGE="ami-9887c6e7"
DYNAMO_TABLE="csye6225"

export vpcId=$(aws ec2 describe-vpcs --query "Vpcs[*].[CidrBlock, VpcId]" --output text|grep 10.0.0.0/16|awk '{print $2}')
export subnetId1=$(aws ec2 describe-subnets --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.1.0/24|grep us-east-1a|awk '{print $1}')
export eC2SecurityGroupId=$(aws ec2 describe-security-groups --query 'SecurityGroups[*].[VpcId, GroupId]' --output text|grep ${vpcId}|awk '{print $2}')

aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=VpcId,ParameterValue=$vpcId ParameterKey=EC2Name,ParameterValue=$EC2_Name ParameterKey=EC2SecurityGroup,ParameterValue=$eC2SecurityGroupId ParameterKey=SubnetId1,ParameterValue=$subnetId1 ParameterKey=EC2VolumeSize,ParameterValue=$EC2VOL_SIZE ParameterKey=EC2VolumeType,ParameterValue=$EC2VOL_TYPE ParameterKey=AMIImage,ParameterValue=$AMI_IMAGE
ParameterKey=DynamoDBName,ParameterValue=$DYNAMO_TABLE

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
