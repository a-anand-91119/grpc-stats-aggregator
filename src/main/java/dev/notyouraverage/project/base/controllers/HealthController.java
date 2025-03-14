package dev.notyouraverage.project.base.controllers;

import dev.notyouraverage.project.base.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
    @Operation(summary = "Health check API", description = "API returns OK while the system is healthy and is ready to accept incoming requests.", tags = {
            "Health Check" }, operationId = "health", responses = @ApiResponse(responseCode = "200", description = "Service is ready for accepting incoming requests"))
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(Constants.OK);
    }

}
