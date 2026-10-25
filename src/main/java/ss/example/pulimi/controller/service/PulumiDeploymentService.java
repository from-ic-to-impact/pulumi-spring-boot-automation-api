package ss.example.pulimi.controller.service;

import com.pulumi.automation.LocalWorkspace;
import com.pulumi.automation.WorkspaceStack;
import com.pulumi.automation.PreviewResult;
import com.pulumi.automation.UpResult;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import com.pulumi.automation.ConfigValue;

@Service
public class PulumiDeploymentService {

    private static final String PROJECT_DIR = "/Users/25647/pulumi";

    private WorkspaceStack getStack(String stackName) throws Exception {
        return LocalWorkspace.createOrSelectStack(
                stackName,
                Paths.get(PROJECT_DIR)
        );
    }

    public String preview(String infrastructure,
                          String bucketName) throws Exception {
        String stackName = getStackName(infrastructure);
        WorkspaceStack stack = getStack(stackName);
        configureInfrastructure(stack, infrastructure, bucketName);

        stack.setConfig(
                "infrastructure:type",
                new ConfigValue(infrastructure.toUpperCase(), false)
        );


        logS3Config(stack);
        PreviewResult result = stack.preview();
        return result.standardOutput();
    }


    private void logS3Config(WorkspaceStack stack) throws Exception {
        System.out.println("S3 bucketName: "
                + stack.getConfig("s3:bucketNames").value());
        System.out.println("S3 bucketName: "
                + stack.getConfig("s3:bucketName").value());

    }

    public String deploy(String infrastructure, String bucketName) throws Exception {
        String stackName = getStackName(infrastructure);
        WorkspaceStack stack = getStack(stackName);
        if ("S3".equalsIgnoreCase(infrastructure)) {
            addS3BucketToConfig(stack, bucketName);
        } else {
            configureInfrastructure(stack, infrastructure, bucketName);
        }

        stack.setConfig(
                "infrastructure:type",
                new ConfigValue(infrastructure.toUpperCase(), false)
        );
        UpResult result = stack.up();

        return result.standardOutput();
    }

    private String getStackName(String infrastructure) {
        return switch (infrastructure.toUpperCase()) {
            case "S3" -> "S3";
            case "VPC" -> "network-dev";
            default -> throw new IllegalArgumentException(
                    "Unsupported infrastructure: " + infrastructure
            );
        };
    }


    private void configureInfrastructure(
            WorkspaceStack stack,
            String infrastructure,
            String bucketName) throws Exception {

        stack.setConfig(
                "infrastructure:type",
                new ConfigValue(infrastructure.toUpperCase(), false)
        );

        if ("S3".equalsIgnoreCase(infrastructure)) {

            if (bucketName == null || bucketName.isBlank()) {
                throw new IllegalArgumentException("Bucket name is required");
            }

            if (!bucketName.matches(
                    "^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$")) {
                throw new IllegalArgumentException("Invalid S3 bucket name");
            }

            List<String> bucketNames = new ArrayList<>();

            // Read the new list, if it exists.
            var existingList = stack.getConfig("s3:bucketNames");

            if (existingList != null && existingList.value() != null
                    && !existingList.value().isBlank()) {
                Arrays.stream(existingList.value().split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(bucketNames::add);
            }

            // If the list is empty, migrate the old single-bucket value.
            if (bucketNames.isEmpty()) {
                var oldBucket = stack.getConfig("s3:bucketName");

                if (oldBucket != null && oldBucket.value() != null
                        && !oldBucket.value().isBlank()) {
                    bucketNames.add(oldBucket.value().trim());
                }
            }

            // Append the requested bucket only if it is not already present.
            if (!bucketNames.contains(bucketName)) {
                bucketNames.add(bucketName);
            }

            stack.setConfig(
                    "s3:bucketNames",
                    new ConfigValue(String.join(",", bucketNames), false)
            );
        }


    }


    private void addS3BucketToConfig(WorkspaceStack stack, String bucketName)
            throws Exception {

        // Read the existing comma-separated bucket list.
        String existingBuckets = "";

        try {
            ConfigValue configValue = stack.getConfig("s3:bucketNames");
            if (configValue != null && configValue.value() != null) {
                existingBuckets = configValue.value();
            }
        } catch (Exception ignored) {
            // No bucketNames config exists yet.
        }

        List<String> bucketNames = new ArrayList<>();

        if (!existingBuckets.isBlank()) {
            Arrays.stream(existingBuckets.split(","))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .forEach(bucketNames::add);
        }

        // Append only if this bucket is not already in the list.
        if (!bucketNames.contains(bucketName)) {
            bucketNames.add(bucketName);
        }

        // Save the complete list back to the stack.
        stack.setConfig(
                "s3:bucketNames",
                new ConfigValue(String.join(",", bucketNames), false)
        );
    }
}


