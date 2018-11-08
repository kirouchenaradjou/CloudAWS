STACK_NAME=$1

bucketName=$(aws s3api list-buckets --query "Buckets[].Name" --output text | grep code-deploy.csye6225-fall2018 | awk '{print $1}')
echo $bucketName
if [ -n "$bucketName" ]
then
    aws s3 rm s3://$bucketName --recursive
fi

aws cloudformation delete-stack --stack-name $STACK_NAME

aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME

if [ $? -ne "0" ]
then 
	echo "Deletion of Stack failed"
else
	echo "Deletion of Stack Success"
fi
