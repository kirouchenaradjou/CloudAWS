STACK_NAME=$1
CODEDEPLOYSERVICEROLENAME="CodeDeployServiceRole"
EC2SERVICEROLENAME="EC2ServiceRole"
CODEDEPLOYPOLICYNAME="CodeDeployPolicy"
S3POLICYNAME="S3Policy"
TRAVISUSER=""
S3BUCKETNAME=""
CODEDEPLOYAPPNAME="CodeDeployApplication"
EC2POLICYNAME="EC2PolicyName"
AWSREGION="us-east-1"
AWSACCOUNTID=""

aws cloudformation create-stack --stack-name $STACK_NAME --capabilities "CAPABILITY_NAMED_IAM" --template-body file://csye6225-cf-cicd.json --parameters ParameterKey=CodeDeployServiceRoleName,ParameterValue=$CODEDEPLOYSERVICEROLENAME ParameterKey=EC2ServiceRoleName,ParameterValue=$EC2SERVICEROLENAME ParameterKey=CodeDeployPolicyName,ParameterValue=$CODEDEPLOYPOLICYNAME ParameterKey=S3PolicyName,ParameterValue=$S3POLICYNAME ParameterKey=TravisUser,ParameterValue=$TRAVISUSER ParameterKey=S3BucketName,ParameterValue=$S3BUCKETNAME ParameterKey=CodeDeployApplicationName,ParameterValue=$CODEDEPLOYAPPNAME ParameterKey=AWSRegion,ParameterValue=$AWSREGION ParameterKey=AWSAccountID,ParameterValue=$AWSACCOUNTID ParameterKey=EC2PolicyName,ParameterValue=$EC2POLICYNAME

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
  STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
