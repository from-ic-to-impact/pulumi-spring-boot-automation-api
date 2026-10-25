# Pulumi Spring Boot Automation API

A Proof of Concept (POC) demonstrating how to automate AWS infrastructure provisioning using **Java, Spring Boot, and the Pulumi Automation API**.

Instead of relying on interactive Pulumi CLI commands, this project exposes REST APIs through a Spring Boot application to preview and deploy infrastructure programmatically.

## Overview

Infrastructure provisioning is often performed through command-line tools or CI/CD pipelines. This POC explores how infrastructure operations can be exposed through REST APIs, allowing other applications or automation workflows to trigger Pulumi deployments programmatically.

The project currently focuses on AWS S3 bucket provisioning, with support for extending the solution to additional infrastructure such as Amazon VPC and EC2.

## Key Features

- **REST API-based infrastructure automation** using Spring Boot.
- **Pulumi Automation API** for programmatically managing infrastructure deployments.
- **Preview infrastructure changes** before applying them.
- **Deploy AWS S3 buckets** using Java.
- **Manage multiple S3 buckets** through Pulumi configuration.
- **Infrastructure as Code (IaC)** using Pulumi and Java.
- **Extensible architecture** for supporting additional AWS resources.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application development |
| Spring Boot | REST API layer |
| Pulumi | Infrastructure as Code |
| Pulumi Automation API | Programmatic infrastructure operations |
| AWS | Cloud infrastructure provider |
| Amazon S3 | Object storage |
| Gradle | Build and dependency management |

## Architecture

```text
Client
  |
  | HTTP Request
  v
Spring Boot REST API
  |
  v
PulumiDeploymentService
  |
  v
Pulumi Automation API
  |
  v
Pulumi Infrastructure Program
  |
  v
AWS Cloud
  |
  +-- Amazon S3
  |
  +-- VPC / EC2 (planned or under development)
```

### Request Flow

1. A client sends an HTTP request to the Spring Boot application.
2. The REST controller receives the request.
3. The deployment service selects the appropriate Pulumi stack and configures the requested infrastructure.
4. The Pulumi Automation API previews or applies the infrastructure changes.
5. Pulumi communicates with AWS to manage the requested resources.
6. The application returns the operation result to the client.

## Prerequisites

Before running this project, ensure you have:

- Java Development Kit (JDK) compatible with the project configuration.
- Git.
- AWS account with permissions to manage the resources you intend to deploy.
- AWS credentials configured securely on your development machine.
- Pulumi CLI installed and configured.
- Gradle Wrapper included in this repository.

You can find the official installation and setup instructions here:

- [Java](https://www.oracle.com/java/technologies/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Pulumi](https://www.pulumi.com/docs/)
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- [Gradle](https://gradle.org/)

## AWS Configuration

Configure AWS authentication using a supported mechanism, such as an AWS CLI profile or environment-based credentials.

For example, if you use the AWS CLI:

```bash
aws configure
```

Verify that AWS authentication works:

```bash
aws sts get-caller-identity
```

Configure the AWS region used by your Pulumi stack. This project has been configured to use `us-east-1` during development.

**Security note:** Never commit AWS access keys, secret keys, session tokens, or other credentials to GitHub. Use secure credential providers and keep secrets out of source control.

## Pulumi Configuration

This project uses Pulumi YAML configuration files for its project and stacks.

The repository may contain files such as:

```text
Pulumi.yaml
Pulumi.S3.yaml
Pulumi.network-dev.yaml
```

These files define the Pulumi project and stack-specific configuration.

Before deploying, verify that the intended stack is selected and that the required AWS region and infrastructure configuration are set.

For example:

```bash
pulumi stack ls
pulumi stack select S3
pulumi config
```

The stack name and configuration keys must match those expected by the application and Pulumi infrastructure program.

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/pulumi-spring-boot-automation-api.git
cd pulumi-spring-boot-automation-api
```

Replace `YOUR_USERNAME` with your GitHub username.

### 2. Build the project

On macOS or Linux:

```bash
./gradlew clean build
```

On Windows:

```bash
gradlew.bat clean build
```

### 3. Start the Spring Boot application

```bash
./gradlew bootRun
```

The application starts on the port configured in the Spring Boot application. Unless changed in the project configuration, Spring Boot typically uses port `8080`.

## REST API

The application is designed to expose REST endpoints for previewing and deploying infrastructure.

The intended endpoint structure is:

| Operation | Example endpoint | Purpose |
|---|---|---|
| Preview S3 infrastructure | `/api/infrastructure/S3/preview` | Preview the proposed infrastructure changes |
| Deploy S3 infrastructure | `/api/infrastructure/S3/deploy` | Apply the infrastructure changes |
| Preview VPC infrastructure | `/api/infrastructure/VPC/preview` | Preview VPC changes, if implemented |
| Deploy VPC infrastructure | `/api/infrastructure/VPC/deploy` | Deploy VPC resources, if implemented |

**Note:** These are illustrative endpoint conventions. Confirm the actual controller mappings and request payloads in the source code before using them. The VPC endpoints are examples for the planned extension unless they have already been implemented.

### Example Workflow

The intended workflow is:

1. Submit an infrastructure request.
2. Preview the changes.
3. Review the proposed additions, modifications, and deletions.
4. Submit the deployment request after reviewing the changes.
5. Verify the resulting infrastructure in AWS.

For S3 provisioning, ensure that each requested bucket name is globally available and complies with AWS S3 naming rules.

## Important Deployment Considerations

- A preview does not itself create AWS resources.
- Review preview output carefully before deploying.
- Run a fresh preview if the requested configuration changes.
- Avoid unintentionally removing existing infrastructure when changing stack configuration.
- Keep stack state and configuration consistent with the resources managed by Pulumi.
- Use appropriate IAM permissions and secure credential management.
- Add authentication and authorization before exposing deployment endpoints to untrusted clients.
- Consider request validation, error handling, logging, and deployment concurrency controls before using the application beyond a POC.

## Project Structure

```text
pulumi-spring-boot-automation-api/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── gradle/
├── gradlew
├── gradlew.bat
├── build.gradle
├── settings.gradle
├── Pulumi.yaml
├── Pulumi.S3.yaml
├── Pulumi.network-dev.yaml
├── .gitignore
└── README.md
```

The exact Java package structure and configuration files may vary as the project evolves.

## Current Scope

The primary goal of this POC is to demonstrate how Pulumi infrastructure operations can be initiated through a Java Spring Boot REST API.

Current focus:

- Spring Boot application integration with Pulumi Automation API.
- Programmatic Pulumi stack operations.
- Previewing infrastructure changes.
- AWS S3 bucket provisioning.
- Managing bucket configuration.

## Future Enhancements

Potential improvements include:

- Complete VPC and EC2 provisioning workflows.
- Improved separation between preview and deployment configuration.
- REST API request validation and structured responses.
- Centralized exception handling and observability.
- API authentication and authorization.
- Deployment status tracking.
- Automated tests.
- CI/CD integration.
- Infrastructure cost and security checks.

## Disclaimer

This repository is a learning and experimentation POC. Review all infrastructure changes before deployment and adapt security, reliability, and operational controls before using it in production.

## Author

Created as part of an exploration of Java-based cloud infrastructure automation and API-driven engineering workflows.
