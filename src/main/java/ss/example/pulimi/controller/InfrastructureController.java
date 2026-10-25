package ss.example.pulimi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ss.example.pulimi.controller.service.PulumiDeploymentService;
import ss.example.pulimi.controller.service.S3BucketRequest;

@RestController
@RequestMapping("/api/infrastructure")
public class InfrastructureController {
    private final PulumiDeploymentService pulumiService;

    public InfrastructureController(PulumiDeploymentService pulumiService) {
        this.pulumiService = pulumiService;
    }

    @PostMapping("/{type}/preview")
    public ResponseEntity<?> preview(@PathVariable String type,
                                     @RequestBody(required = false) S3BucketRequest request) {
        try {
            String bucketName =
                    "S3".equalsIgnoreCase(type) && request != null
                            ? request.getBucketName()
                            : null;

            return ResponseEntity.ok(
                    pulumiService.preview(type, bucketName)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Preview failed: " + e.getMessage());
        }
    }

    @PostMapping("/{type}/deploy")
    public ResponseEntity<?> deploy(
            @PathVariable String type,
            @RequestBody(required = false) S3BucketRequest request) {

        try {
            String bucketName =
                    "S3".equalsIgnoreCase(type) && request != null
                            ? request.getBucketName()
                            : null;

            return ResponseEntity.ok(
                    pulumiService.deploy(type, bucketName)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Deployment failed: " + e.getMessage());
        }
    }
}
