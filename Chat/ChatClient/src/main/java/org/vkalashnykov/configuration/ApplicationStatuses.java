package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 29.12.16.
 */
public enum ApplicationStatuses {
    REGISTER_SUCCESS("Registered successfully"),
    REGISTER_ERROR("Registration fault"),
    CHANGE_SUCCESS("Data modification successfully "),
    CHANGE_ERROR("Error modification data. ");

    private String statusDescription;

    ApplicationStatuses(String statusDescription) {
        this.statusDescription=statusDescription;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
