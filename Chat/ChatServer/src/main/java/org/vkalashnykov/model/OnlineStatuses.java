package org.vkalashnykov.model;

/**
 * Created by vkalashnykov on 25.12.16.
 */
public enum OnlineStatuses {
    ONLINE("Online"), NOT_ACTIVE("Not active");

    private String statusDescription;

    public String getStatusDescription() {
        return statusDescription;
    }

    OnlineStatuses(String statusDescription) {
        this.statusDescription=statusDescription;
    }
}
