package org.vkalashnykov.model;

/**
 * Created by vkalashnykov on 25.12.16.
 */
public enum OnlineStatuses {
    ONLINE("Online"), OFFLINE("Offline");

    private String statusDescription;

    public String getStatusDescription() {
        return statusDescription;
    }

    OnlineStatuses(String statusDescription) {
        this.statusDescription=statusDescription;
    }
}
