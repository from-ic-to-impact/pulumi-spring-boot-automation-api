package ss.example.pulimi.controller;


import com.pulumi.aws.ec2.Vpc;
import com.pulumi.aws.ec2.VpcArgs;
import com.pulumi.aws.ec2.Subnet;
import com.pulumi.aws.ec2.SubnetArgs;
import com.pulumi.aws.ec2.InternetGateway;
import com.pulumi.aws.ec2.InternetGatewayArgs;
import com.pulumi.aws.ec2.RouteTable;
import com.pulumi.aws.ec2.RouteTableArgs;
import com.pulumi.aws.ec2.RouteTableAssociation;
import com.pulumi.aws.ec2.RouteTableAssociationArgs;
import com.pulumi.aws.ec2.SecurityGroup;
import com.pulumi.aws.ec2.SecurityGroupArgs;
import com.pulumi.aws.ec2.inputs.RouteTableRouteArgs;
import com.pulumi.aws.ec2.inputs.SecurityGroupEgressArgs;

import java.util.Map;

public class NetworkInfrastructure {

    public final Vpc vpc;
    public final Subnet subnet;
    public final SecurityGroup securityGroup;

    public NetworkInfrastructure() {

        // 1. Create VPC
        vpc = new Vpc("learning-vpc",
                VpcArgs.builder()
                        .cidrBlock("10.0.0.0/16")
                        .tags(Map.of("Name", "learning-vpc"))
                        .build());

        // 2. Create public subnet
        subnet = new Subnet("learning-subnet",
                SubnetArgs.builder()
                        .vpcId(vpc.id())
                        .cidrBlock("10.0.1.0/24")
                        .mapPublicIpOnLaunch(true)
                        .tags(Map.of("Name", "learning-subnet"))
                        .build());

        // 3. Create Internet Gateway
        var igw = new InternetGateway("learning-igw",
                InternetGatewayArgs.builder()
                        .vpcId(vpc.id())
                        .build());

        // 4. Create route table
        var routeTable = new RouteTable("learning-route-table",
                RouteTableArgs.builder()
                        .vpcId(vpc.id())
                        .routes(RouteTableRouteArgs.builder()
                                .cidrBlock("0.0.0.0/0")
                                .gatewayId(igw.id())
                                .build())
                        .build());

        // 5. Associate subnet with route table
        new RouteTableAssociation("learning-route-association",
                RouteTableAssociationArgs.builder()
                        .subnetId(subnet.id())
                        .routeTableId(routeTable.id())
                        .build());

        // 6. Create security group
        securityGroup = new SecurityGroup("learning-security-group",
                SecurityGroupArgs.builder()
                        .vpcId(vpc.id())
                        .description("Learning EC2 security group")
                        .egress(SecurityGroupEgressArgs.builder()
                                .fromPort(0)
                                .toPort(0)
                                .protocol("-1")
                                .cidrBlocks("0.0.0.0/0")
                                .build())
                        .tags(Map.of("Name", "learning-security-group"))
                        .build());
    }
}