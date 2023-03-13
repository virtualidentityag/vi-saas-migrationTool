
package com.vi.migrationtool.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Settings for the initializing of the monitoring
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "initializeMonitoring",
    "monitoringTemplateFile"
})
public class Monitoring {

    /**
     * Indicates whether the monitoring should be initialized for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("initializeMonitoring")
    @JsonPropertyDescription("Indicates whether the monitoring should be initialized for this consulting type")
    private Boolean initializeMonitoring = false;
    /**
     * The path to the template file for the initialization of the monitoring for this consulting type
     * 
     */
    @JsonProperty("monitoringTemplateFile")
    @JsonPropertyDescription("The path to the template file for the initialization of the monitoring for this consulting type")
    private String monitoringTemplateFile = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Monitoring() {
    }

    /**
     * 
     * @param initializeMonitoring
     * @param monitoringTemplateFile
     */
    public Monitoring(Boolean initializeMonitoring, String monitoringTemplateFile) {
        super();
        this.initializeMonitoring = initializeMonitoring;
        this.monitoringTemplateFile = monitoringTemplateFile;
    }

    /**
     * Indicates whether the monitoring should be initialized for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("initializeMonitoring")
    public Boolean getInitializeMonitoring() {
        return initializeMonitoring;
    }

    /**
     * Indicates whether the monitoring should be initialized for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("initializeMonitoring")
    public void setInitializeMonitoring(Boolean initializeMonitoring) {
        this.initializeMonitoring = initializeMonitoring;
    }

    public Monitoring withInitializeMonitoring(Boolean initializeMonitoring) {
        this.initializeMonitoring = initializeMonitoring;
        return this;
    }

    /**
     * The path to the template file for the initialization of the monitoring for this consulting type
     * 
     */
    @JsonProperty("monitoringTemplateFile")
    public String getMonitoringTemplateFile() {
        return monitoringTemplateFile;
    }

    /**
     * The path to the template file for the initialization of the monitoring for this consulting type
     * 
     */
    @JsonProperty("monitoringTemplateFile")
    public void setMonitoringTemplateFile(String monitoringTemplateFile) {
        this.monitoringTemplateFile = monitoringTemplateFile;
    }

    public Monitoring withMonitoringTemplateFile(String monitoringTemplateFile) {
        this.monitoringTemplateFile = monitoringTemplateFile;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(initializeMonitoring).append(monitoringTemplateFile).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Monitoring) == false) {
            return false;
        }
        Monitoring rhs = ((Monitoring) other);
        return new EqualsBuilder().append(initializeMonitoring, rhs.initializeMonitoring).append(monitoringTemplateFile, rhs.monitoringTemplateFile).isEquals();
    }

}
