package com.ultronai.controller;

import com.ultronai.dto.request.CreateWorkflowEdgeRequest;
import com.ultronai.dto.request.CreateWorkflowNodeRequest;
import com.ultronai.dto.request.CreateWorkflowRequest;
import com.ultronai.dto.response.WorkflowEdgeResponse;
import com.ultronai.dto.response.WorkflowNodeResponse;
import com.ultronai.dto.response.WorkflowResponse;
import com.ultronai.dto.response.WorkflowValidationResponse;
import com.ultronai.security.UserPrincipal;
import com.ultronai.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> listWorkflows(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workflowService.listWorkflows(principal.getTenantId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResponse> getWorkflow(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workflowService.getWorkflow(id, principal.getTenantId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<WorkflowResponse> createWorkflow(
        @Valid @RequestBody CreateWorkflowRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(request, principal.getTenantId()));
    }

    @PostMapping("/{id}/nodes")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<WorkflowNodeResponse> addNode(
        @PathVariable Long id,
        @Valid @RequestBody CreateWorkflowNodeRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.addNode(id, request, principal.getTenantId()));
    }

    @PostMapping("/{id}/edges")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<WorkflowEdgeResponse> addEdge(
        @PathVariable Long id,
        @Valid @RequestBody CreateWorkflowEdgeRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.addEdge(id, request, principal.getTenantId()));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<WorkflowValidationResponse> validateWorkflow(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(workflowService.validateWorkflow(id, principal.getTenantId()));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<WorkflowResponse> publishWorkflow(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(workflowService.publishWorkflow(id, principal.getTenantId()));
    }
}
