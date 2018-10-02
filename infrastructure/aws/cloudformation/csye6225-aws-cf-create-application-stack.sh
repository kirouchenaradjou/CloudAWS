STACK_NAME=$1
VPC_NAME=${STACK_NAME}-csye6225-vpc

aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=VPCName,ParameterValue=$VPC_NAME
export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"