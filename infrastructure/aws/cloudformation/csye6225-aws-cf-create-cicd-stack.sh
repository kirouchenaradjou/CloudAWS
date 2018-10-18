STACK_NAME=$1
CODEDEPLOYSERVICEROLENAME="CodeDeployServiceRole"
EC2SERVICEROLENAME="EC2ServiceRole"
CODEDEPLOYPOLICYNAME="CodeDeployPolicy"
S3POLICYNAME="S3Policy"
TRAVISUSER="TravisUser"
S3BUCKETNAME="haha.test"
CODEDEPLOYAPPNAME="CodeDeployApplication"

aws cloudformation create-stack $STACK_NAME --template-body file://csye6225-cf-ci-cd.json --parameters ParameterKey=CodeDeployServiceRoleName,ParameterValue=$CODEDEPLOYSERVICEROLENAME ParameterKey=EC2ServiceRoleName,ParameterValue=$EC2SERVICEROLENAME ParameterKey=CodeDeployPolicyName,ParameterValue=$CODEDEPLOYPOLICYNAME ParameterKey=S3PolicyName,ParameterValue=$S3POLICYNAME ParameterKey=TravisUser,ParameterValue=$TRAVISUSER ParameterKey=S3BucketName,ParameterValue=$S3BUCKETNAME ParameterKey=CodeDeployApplicationName,ParameterValue=$CODEDEPLOYAPPNAME

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
  STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
