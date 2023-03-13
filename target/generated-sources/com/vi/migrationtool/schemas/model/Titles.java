
package com.vi.migrationtool.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Titles for this consulting type, which are displayed to the user
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "default",
    "short",
    "long",
    "welcome",
    "registrationDropdown"
})
public class Titles {

    /**
     * Title which is displayed in list items
     * (Required)
     * 
     */
    @JsonProperty("default")
    @JsonPropertyDescription("Title which is displayed in list items")
    private String _default;
    /**
     * Title which is displayed in the session header
     * (Required)
     * 
     */
    @JsonProperty("short")
    @JsonPropertyDescription("Title which is displayed in the session header")
    private String _short;
    /**
     * Title which is displayed as overline and as title of the registration form
     * (Required)
     * 
     */
    @JsonProperty("long")
    @JsonPropertyDescription("Title which is displayed as overline and as title of the registration form")
    private String _long;
    /**
     * Welcome message which is displayed on the pre-registration page
     * (Required)
     * 
     */
    @JsonProperty("welcome")
    @JsonPropertyDescription("Welcome message which is displayed on the pre-registration page")
    private String welcome;
    /**
     * Alternative title for the display of the topic for the registration dropdown in the advice seeker profile
     * 
     */
    @JsonProperty("registrationDropdown")
    @JsonPropertyDescription("Alternative title for the display of the topic for the registration dropdown in the advice seeker profile")
    private String registrationDropdown = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Titles() {
    }

    /**
     * 
     * @param _default
     * @param registrationDropdown
     * @param _long
     * @param _short
     * @param welcome
     */
    public Titles(String _default, String _short, String _long, String welcome, String registrationDropdown) {
        super();
        this._default = _default;
        this._short = _short;
        this._long = _long;
        this.welcome = welcome;
        this.registrationDropdown = registrationDropdown;
    }

    /**
     * Title which is displayed in list items
     * (Required)
     * 
     */
    @JsonProperty("default")
    public String getDefault() {
        return _default;
    }

    /**
     * Title which is displayed in list items
     * (Required)
     * 
     */
    @JsonProperty("default")
    public void setDefault(String _default) {
        this._default = _default;
    }

    public Titles withDefault(String _default) {
        this._default = _default;
        return this;
    }

    /**
     * Title which is displayed in the session header
     * (Required)
     * 
     */
    @JsonProperty("short")
    public String getShort() {
        return _short;
    }

    /**
     * Title which is displayed in the session header
     * (Required)
     * 
     */
    @JsonProperty("short")
    public void setShort(String _short) {
        this._short = _short;
    }

    public Titles withShort(String _short) {
        this._short = _short;
        return this;
    }

    /**
     * Title which is displayed as overline and as title of the registration form
     * (Required)
     * 
     */
    @JsonProperty("long")
    public String getLong() {
        return _long;
    }

    /**
     * Title which is displayed as overline and as title of the registration form
     * (Required)
     * 
     */
    @JsonProperty("long")
    public void setLong(String _long) {
        this._long = _long;
    }

    public Titles withLong(String _long) {
        this._long = _long;
        return this;
    }

    /**
     * Welcome message which is displayed on the pre-registration page
     * (Required)
     * 
     */
    @JsonProperty("welcome")
    public String getWelcome() {
        return welcome;
    }

    /**
     * Welcome message which is displayed on the pre-registration page
     * (Required)
     * 
     */
    @JsonProperty("welcome")
    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public Titles withWelcome(String welcome) {
        this.welcome = welcome;
        return this;
    }

    /**
     * Alternative title for the display of the topic for the registration dropdown in the advice seeker profile
     * 
     */
    @JsonProperty("registrationDropdown")
    public String getRegistrationDropdown() {
        return registrationDropdown;
    }

    /**
     * Alternative title for the display of the topic for the registration dropdown in the advice seeker profile
     * 
     */
    @JsonProperty("registrationDropdown")
    public void setRegistrationDropdown(String registrationDropdown) {
        this.registrationDropdown = registrationDropdown;
    }

    public Titles withRegistrationDropdown(String registrationDropdown) {
        this.registrationDropdown = registrationDropdown;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_default).append(_short).append(_long).append(welcome).append(registrationDropdown).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Titles) == false) {
            return false;
        }
        Titles rhs = ((Titles) other);
        return new EqualsBuilder().append(_default, rhs._default).append(_short, rhs._short).append(_long, rhs._long).append(welcome, rhs.welcome).append(registrationDropdown, rhs.registrationDropdown).isEquals();
    }

}
