package com.ultronai.controller;

import com.ultronai.dto.response.IntentResponse;
import com.ultronai.exception.DuplicateResourceException;
import com.ultronai.exception.ResourceNotFoundException;
import com.ultronai.model.entity.Intent;
import com.ultronai.model.entity.IntentExample;
import com.ultronai.model.entity.Tenant;
import com.ultronai.repository.IntentExampleRepository;
import com.ultronai.repository.IntentRepository;
import com.ultronai.repository.TenantRepository;
import com.ultronai.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/intents")
public class IntentController {

    private final IntentRepository intentRepository;
    private final IntentExampleRepository intentExampleRepository;
    private final TenantRepository tenantRepository;

    public IntentController(
        IntentRepository intentRepository,
        IntentExampleRepository intentExampleRepository,
        TenantRepository tenantRepository
    ) {
        this.intentRepository = intentRepository;
        this.intentExampleRepository = intentExampleRepository;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping
    public ResponseEntity<List<IntentResponse>> listIntents(@AuthenticationPrincipal UserPrincipal principal) {
        List<Intent> intents = intentRepository.findByTenantId(principal.getTenantId());
        List<IntentResponse> responses = intents.stream()
            .map(this::mapToIntentResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<IntentResponse> createIntent(
        @Valid @RequestBody CreateIntentDto dto,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (intentRepository.existsByTenantIdAndName(principal.getTenantId(), dto.getName())) {
            throw new DuplicateResourceException("Intent with name '" + dto.getName() + "' already exists for this tenant");
        }

        Tenant tenant = tenantRepository.findById(principal.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Intent intent = new Intent(tenant, dto.getName(), dto.getDescription());
        intent = intentRepository.save(intent);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToIntentResponse(intent));
    }

    @PostMapping("/{id}/examples")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<Void> addExample(
        @PathVariable Long id,
        @Valid @RequestBody CreateExampleDto dto,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Intent intent = intentRepository.findByIdAndTenantId(id, principal.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Intent not found"));

        Tenant tenant = intent.getTenant();
        IntentExample example = new IntentExample(tenant, intent, dto.getText());
        intentExampleRepository.save(example);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private IntentResponse mapToIntentResponse(Intent intent) {
        return new IntentResponse(
            intent.getId(),
            intent.getTenant().getId(),
            intent.getName(),
            intent.getDescription(),
            intent.getStatus(),
            intent.getCreatedAt()
        );
    }

    public static class CreateIntentDto {
        @NotBlank
        private String name;
        private String description;

        public CreateIntentDto() {
        }

        public CreateIntentDto(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class CreateExampleDto {
        @NotBlank
        private String text;

        public CreateExampleDto() {
        }

        public CreateExampleDto(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
