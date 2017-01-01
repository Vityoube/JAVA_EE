package org.vkalashnykov.model;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public enum ChannelStatuses {
    USER("User"), VIP("VIP"), MODERATOR("Moderator");

    private String statusName;

    ChannelStatuses(String statusName) {
        this.statusName=statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
