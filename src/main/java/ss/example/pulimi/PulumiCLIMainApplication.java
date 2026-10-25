package ss.example.pulimi;

import com.pulumi.Context;
import com.pulumi.Pulumi;
import com.pulumi.aws.ec2.Ec2Functions;
import com.pulumi.aws.ec2.Instance;
import com.pulumi.aws.ec2.InstanceArgs;
import com.pulumi.aws.ec2.inputs.GetAmiArgs;
import com.pulumi.aws.ec2.inputs.GetAmiFilterArgs;
import com.pulumi.aws.ec2.outputs.GetAmiResult;
import com.pulumi.aws.s3.Bucket;
import com.pulumi.aws.s3.BucketArgs;
import com.pulumi.core.Output;
import com.pulumi.Config;
import ss.example.pulimi.controller.NetworkInfrastructure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PulumiCLIMainApplication {
    public static void main(String[] args) {
        Pulumi.run(ctx -> {
            String type = ctx.config("infrastructure")
                    .get("type")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Missing infrastructure:type configuration"));

            switch (type.toUpperCase()) {
                case "S3" -> createS3(ctx);
                case "VPC" -> createVpc(ctx);
                default -> throw new IllegalArgumentException(
                        "Unsupported infrastructure type: " + type);
            }
        });

    }


    public static void createS3(Context ctx) {
        Config config = ctx.config("s3");
        List<String> bucketNames = config.get("bucketNames")
                .map(value -> Arrays.stream(value.split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .distinct()
                        .toList())
                .orElse(List.of());

        if (bucketNames.isEmpty()) {
            bucketNames = config.get("bucketName")
                    .filter(name -> !name.isBlank())
                    .map(List::of)
                    .orElse(List.of());
        }
        for (String bucketName : bucketNames) {
            new Bucket(
                    "s3-bucket-" + bucketName,
                    BucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        }

        ctx.export("bucketNames", bucketNames);
    }



    private static void createVpc(Context ctx) {
        // Create networking infrastructure
        var network = new NetworkInfrastructure();

        // Find the latest Amazon Linux 2023 AMI
        var ami = Ec2Functions.getAmi(GetAmiArgs.builder()
                .owners("amazon")
                .mostRecent(true)
                .filters(
                        GetAmiFilterArgs.builder()
                                .name("name")
                                .values("al2023-ami-2023.*-x86_64")
                                .build(),
                        GetAmiFilterArgs.builder()
                                .name("architecture")
                                .values("x86_64")
                                .build()
                )
                .build());

        // Create EC2 instance
        var instance = new Instance("learning-ec2",
                InstanceArgs.builder()
                        .ami(ami.applyValue(GetAmiResult::id))
                        .instanceType("t3.micro")
                        .subnetId(network.subnet.id())
                        .vpcSecurityGroupIds(
                                Output.all(network.securityGroup.id()))
                        .associatePublicIpAddress(true)
                        .tags(Map.of("Name", "learning-ec2"))
                        .build());

        // Export outputs
        ctx.export("vpcId", network.vpc.id());
        ctx.export("subnetId", network.subnet.id());
        ctx.export("instanceId", instance.id());
        ctx.export("publicIp", instance.publicIp());

    }

}
