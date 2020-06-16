package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class Approve extends BaseEntity
{
    private Long id;

    private Long user_id;

    private String client_id;

    private boolean status;

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }

    public String getClientId() {
        return client_id;
    }

    public void setClientId(String clientId) {
        this.client_id = clientId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
