package com.ultronai.dto.response;

import com.ultronai.model.enums.TenantStatus;
import java.time.LocalDateTime;

public class TenantResponse {

    private Long id;
    private String name;
    private String slug;
    private TenantStatus status;
    private LocalDateTime createdAt;

    public TenantResponse() {
    }

    public TenantResponse(Long id, String name, String slug, TenantStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
