#!/bin/bash -e

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
else
	echo "Started with deleting resources"
fi


ROUTETABLENAME="$1-csye6225-public-route-table"

ROUTETABLEID=$(aws ec2 describe-route-tables --filters Name=tag:Name,Values=$ROUTETABLENAME --query RouteTables[0].RouteTableId --output text)

if [ $? -eq 0 ]
then
	echo "Success describe route table id"
else
	echo "Fail describe route table id"
	exit 1
fi


RC=$(aws ec2 delete-route --route-table-id $ROUTETABLEID --destination-cidr-block 0.0.0.0/0) || echo "Stack with vpc $1 doesn't exist"

if [ $? -eq 0 ]
then
	echo "Success delete route"
else
	echo "Fail delete route"
	exit 1
fi

RC=$(aws ec2 delete-route-table --route-table-id $ROUTETABLEID)

if [ $? -eq 0 ]
then
	echo "Success delete route table"
else
	echo "Fail delete route table"
	exit 1
fi


IGWNAME="$1-csye6225-InternetGateway"

VPCNAME="$1-csye6225-vpc"

VPCID=$(aws ec2 describe-vpcs --filters Name=tag:Name,Values=$VPCNAME --query Vpcs[0].VpcId --output text)

if [ $? -eq 0 ]
then
	echo "Success describe vpc-id"
else
	echo "Fail describe vpc id"
	exit 1
fi

IGWID=$(aws ec2 describe-internet-gateways --filters Name=attachment.vpc-id,Values=$VPCID --query InternetGateways[0].InternetGatewayId --output text)

if [ $? -eq 0 ]
then
	echo "Success describe internet gateway"
else
	echo "Fail describe internet gateway id"
	exit 1
fi

RC=$(aws ec2 detach-internet-gateway --internet-gateway-id $IGWID --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Success detach internet gateway"
else
	echo "Fail detach internet gateway"
	exit 1
fi

RC=$(aws ec2 delete-internet-gateway --internet-gateway-id $IGWID)

if [ $? -eq 0 ]
then
	echo "Success delete internet gateway"
else
	echo "Fail delete internet gateway"
	exit 1
fi

RC=$(aws ec2 delete-vpc --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Success delete vpc"
else
	echo "Fail delete vpc"
	exit 1
fi

echo "Resources deletion complete. VpcId $VPCID"
exit 0
