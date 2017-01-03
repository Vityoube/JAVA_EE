package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public enum MessageStatuses {
    NORMAL("Normal"),SYSTEM("System");

    private String statusName;



    MessageStatuses(String statusName) {
        this.statusName=statusName;
    }

    public String getStatusName() {
        return statusName;
    }


}
