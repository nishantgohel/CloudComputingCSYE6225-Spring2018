#!/bin/bash

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
fi

echo "Creating Stack .."
VPCNAME="$1-csye6225-vpc"

RC=$(aws ec2 create-vpc --cidr-block 10.0.0.0/16)

if [ $? -eq 0 ]
then
	echo "Success: VPC created"
else
	echo "Fail VPC creation"
	exit 1
fi


VPCID=$(aws ec2 describe-vpcs  --filters Name=cidr,Values=10.0.0.0/16 --query Vpcs[0].VpcId --output text)

if [ $? -eq 0 ]
then
	echo "Success: VPC described"
else
	echo "Fail describe vpc"
	exit 1
fi

# echo $VPCID
# echo $VPCNAME

RC=$(aws ec2 create-tags --resources $VPCID --tags Key=Name,Value=$VPCNAME)

if [ $? -eq 0 ]
then
	echo "Success: Tags created"
else
	echo "Fail create tag for vpc"
	exit 1
fi


IGWNAME="$1-csye6225-InternetGateway"
RC=$(aws ec2 create-internet-gateway)

if [ $? -eq 0 ]
then
	echo "Success: IG created"
else
	echo "Fail create internet gateway"
	exit 1
fi

IGWID=$(aws ec2 describe-internet-gateways --query 'InternetGateways[?Attachments[0].State != `available`]'.InternetGatewayId --output text)

if [ $? -eq 0 ]
then
	echo "Success: IG described"
else
	echo "Fail describe internet gateway"
	exit 1
fi

RC=$(aws ec2 create-tags --resources $IGWID --tags Key=Name,Value=$IGWNAME)

if [ $? -eq 0 ]
then
	echo "Success: IGW"
else
	echo "Fail create tags for IGW"
	exit 1
fi

RC=$(aws ec2 attach-internet-gateway --internet-gateway-id $IGWID --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Success: gateway attached"
else
	echo "Fail attach gateway"
	exit 1
fi

ROUTETABLENAME="$1-csye6225-public-route-table"
RC=$(aws ec2 create-route-table --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Success: RouteTables"
else
	echo "Fail create route table"
	exit 1
fi

ROUTETABLEID=$(aws ec2 describe-route-tables --filters Name=vpc-id,Values=$VPCID --query 'RouteTables[?Associations[0].Main != `true`]'.RouteTableId --output text)

if [ $? -eq 0 ]
then
	echo "Success: Route table described"
else
	echo "Fail describe route table"
	exit 1
fi

RC=$(aws ec2 create-tags --resources $ROUTETABLEID --tags Key=Name,Value=$ROUTETABLENAME)

if [ $? -eq 0 ]
then
	echo "Success: Route table tagged"
else
	echo "Fail create tag for route table"
	exit 1
fi

RC=$(aws ec2 create-route --route-table-id $ROUTETABLEID --destination-cidr-block 0.0.0.0/0 --gateway-id $IGWID)

if [ $? -eq 0 ]
then
	echo "Success: Route created"
else
	echo "Fail create route"
	exit 1
fi

echo "Stack creation process complete. VPC id $VPCID"