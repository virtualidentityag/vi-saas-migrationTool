
package com.vi.migrationtool.schemas.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Consulting type
 * <p>
 * Settings for a consulting type
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "tenantId",
    "description",
    "groups",
    "furtherInformation",
    "slug",
    "excludeNonMainConsultantsFromTeamSessions",
    "lockedAgencies",
    "whiteSpot",
    "groupChat",
    "consultantBoundedToConsultingType",
    "welcomeMessage",
    "sendFurtherStepsMessage",
    "sendSaveSessionDataMessage",
    "isSetEmailAllowed",
    "sessionDataInitializing",
    "monitoring",
    "initializeFeedbackChat",
    "isPeerChat",
    "languageFormal",
    "roles",
    "notifications",
    "registration",
    "titles",
    "urls",
    "showAskerProfile",
    "isVideoCallAllowed",
    "isSubsequentRegistrationAllowed",
    "isAnonymousConversationAllowed",
    "voluntaryComponents",
    "requiredComponents",
    "welcomeScreen"
})
public class ConsultingType {

    /**
     * The unique identifier for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("The unique identifier for the consulting type")
    private Integer id;
    /**
     * The unique identifier of the tenant that the consulting type belongs to.
     * 
     */
    @JsonProperty("tenantId")
    @JsonPropertyDescription("The unique identifier of the tenant that the consulting type belongs to.")
    private Integer tenantId = null;
    /**
     * Description for the consulting type
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("Description for the consulting type")
    private String description = null;
    /**
     * Group names - the same group names are combined in the consulting type structure
     * 
     */
    @JsonProperty("groups")
    @JsonPropertyDescription("Group names - the same group names are combined in the consulting type structure")
    private List<String> groups = new ArrayList<String>();
    /**
     * Label and link for further information of the consulting type
     * 
     */
    @JsonProperty("furtherInformation")
    @JsonPropertyDescription("Label and link for further information of the consulting type")
    private FurtherInformation furtherInformation = null;
    /**
     * The unique slug for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("slug")
    @JsonPropertyDescription("The unique slug for the consulting type")
    private String slug;
    /**
     * True, if only consultants with role main-consultant should be added to all team sessions
     * 
     */
    @JsonProperty("excludeNonMainConsultantsFromTeamSessions")
    @JsonPropertyDescription("True, if only consultants with role main-consultant should be added to all team sessions")
    private Boolean excludeNonMainConsultantsFromTeamSessions = false;
    /**
     * If true, no agency with this consulting type can be deleted or set offline via the admin api
     * 
     */
    @JsonProperty("lockedAgencies")
    @JsonPropertyDescription("If true, no agency with this consulting type can be deleted or set offline via the admin api")
    private Boolean lockedAgencies = false;
    /**
     * Behavior regarding the white spots agencies
     * 
     */
    @JsonProperty("whiteSpot")
    @JsonPropertyDescription("Behavior regarding the white spots agencies")
    private WhiteSpot whiteSpot;
    /**
     * Group chat settings
     * 
     */
    @JsonProperty("groupChat")
    @JsonPropertyDescription("Group chat settings")
    private GroupChat groupChat;
    /**
     * True, if a consultant may only be assigned to agencies with this consulting type
     * 
     */
    @JsonProperty("consultantBoundedToConsultingType")
    @JsonPropertyDescription("True, if a consultant may only be assigned to agencies with this consulting type")
    private Boolean consultantBoundedToConsultingType = false;
    /**
     * Settings for the welcome message
     * 
     */
    @JsonProperty("welcomeMessage")
    @JsonPropertyDescription("Settings for the welcome message")
    private WelcomeMessage welcomeMessage;
    /**
     * Indicates whether if the system should send an automatic further steps message to all enquiries for this consulting type or not
     * 
     */
    @JsonProperty("sendFurtherStepsMessage")
    @JsonPropertyDescription("Indicates whether if the system should send an automatic further steps message to all enquiries for this consulting type or not")
    private Boolean sendFurtherStepsMessage = true;
    /**
     * Indicates whether if the system should send an automatic message with a prompt for the user to enter optional information to the session
     * 
     */
    @JsonProperty("sendSaveSessionDataMessage")
    @JsonPropertyDescription("Indicates whether if the system should send an automatic message with a prompt for the user to enter optional information to the session")
    private Boolean sendSaveSessionDataMessage = true;
    /**
     * Asker is allowed to set his email when he is registered for at least one consultingType with this setting 'true'
     * 
     */
    @JsonProperty("isSetEmailAllowed")
    @JsonPropertyDescription("Asker is allowed to set his email when he is registered for at least one consultingType with this setting 'true'")
    private Boolean isSetEmailAllowed = true;
    /**
     * Settings to control which optional fields for consultation should be initialized for this consultation type.
     * 
     */
    @JsonProperty("sessionDataInitializing")
    @JsonPropertyDescription("Settings to control which optional fields for consultation should be initialized for this consultation type.")
    private SessionDataInitializing sessionDataInitializing;
    /**
     * Settings for the initializing of the monitoring
     * 
     */
    @JsonProperty("monitoring")
    @JsonPropertyDescription("Settings for the initializing of the monitoring")
    private Monitoring monitoring;
    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("initializeFeedbackChat")
    @JsonPropertyDescription("Indicates whether the feedback chat should be initialized for every session of this consulting type")
    private Boolean initializeFeedbackChat = false;
    /**
     * Indicates whether the chat should be initialized as peer chat for every session of this consulting type
     * 
     */
    @JsonProperty("isPeerChat")
    @JsonPropertyDescription("Indicates whether the chat should be initialized as peer chat for every session of this consulting type")
    private Boolean isPeerChat = false;
    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("languageFormal")
    @JsonPropertyDescription("Indicates whether the feedback chat should be initialized for every session of this consulting type")
    private Boolean languageFormal = true;
    /**
     * Role definitions
     * (Required)
     * 
     */
    @JsonProperty("roles")
    @JsonPropertyDescription("Role definitions")
    private Roles roles;
    /**
     * Settings for the mail notifications
     * 
     */
    @JsonProperty("notifications")
    @JsonPropertyDescription("Settings for the mail notifications")
    private Notifications notifications;
    /**
     * Settings for the registration process
     * (Required)
     * 
     */
    @JsonProperty("registration")
    @JsonPropertyDescription("Settings for the registration process")
    private Registration registration;
    /**
     * Titles for this consulting type, which are displayed to the user
     * (Required)
     * 
     */
    @JsonProperty("titles")
    @JsonPropertyDescription("Titles for this consulting type, which are displayed to the user")
    private Titles titles;
    /**
     * Forwarding urls
     * (Required)
     * 
     */
    @JsonProperty("urls")
    @JsonPropertyDescription("Forwarding urls")
    private Urls urls;
    /**
     * True to show asker profile including monitoring for consultants. False, when asker profile and monitoring should not be viewable.
     * 
     */
    @JsonProperty("showAskerProfile")
    @JsonPropertyDescription("True to show asker profile including monitoring for consultants. False, when asker profile and monitoring should not be viewable.")
    private Boolean showAskerProfile = true;
    /**
     * True to enable video call buttons for consultants. False, when video call should be deactivated for consulting type.
     * 
     */
    @JsonProperty("isVideoCallAllowed")
    @JsonPropertyDescription("True to enable video call buttons for consultants. False, when video call should be deactivated for consulting type.")
    private Boolean isVideoCallAllowed = false;
    /**
     * If true, askers additionally can register for this consulting type on their profile page at a later date.
     * 
     */
    @JsonProperty("isSubsequentRegistrationAllowed")
    @JsonPropertyDescription("If true, askers additionally can register for this consulting type on their profile page at a later date.")
    private Boolean isSubsequentRegistrationAllowed = true;
    /**
     * Indicates whether anonymous 1:1 chats can be performed.
     * 
     */
    @JsonProperty("isAnonymousConversationAllowed")
    @JsonPropertyDescription("Indicates whether anonymous 1:1 chats can be performed.")
    private Boolean isAnonymousConversationAllowed = false;
    /**
     * Can be provided to display additional fields in the asker profile.
     * 
     */
    @JsonProperty("voluntaryComponents")
    @JsonPropertyDescription("Can be provided to display additional fields in the asker profile.")
    private List<Object> voluntaryComponents = new ArrayList<Object>();
    @JsonProperty("requiredComponents")
    private RequiredComponents requiredComponents;
    /**
     * Properties for registration welcome screen items
     * 
     */
    @JsonProperty("welcomeScreen")
    @JsonPropertyDescription("Properties for registration welcome screen items")
    private WelcomeScreen welcomeScreen = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ConsultingType() {
    }

    /**
     * 
     * @param initializeFeedbackChat
     * @param sessionDataInitializing
     * @param roles
     * @param isVideoCallAllowed
     * @param description
     * @param isSubsequentRegistrationAllowed
     * @param lockedAgencies
     * @param urls
     * @param showAskerProfile
     * @param consultantBoundedToConsultingType
     * @param id
     * @param sendFurtherStepsMessage
     * @param requiredComponents
     * @param slug
     * @param furtherInformation
     * @param welcomeScreen
     * @param groupChat
     * @param isSetEmailAllowed
     * @param languageFormal
     * @param excludeNonMainConsultantsFromTeamSessions
     * @param welcomeMessage
     * @param sendSaveSessionDataMessage
     * @param groups
     * @param whiteSpot
     * @param titles
     * @param monitoring
     * @param isAnonymousConversationAllowed
     * @param voluntaryComponents
     * @param isPeerChat
     * @param tenantId
     * @param registration
     * @param notifications
     */
    public ConsultingType(Integer id, Integer tenantId, String description, List<String> groups, FurtherInformation furtherInformation, String slug, Boolean excludeNonMainConsultantsFromTeamSessions, Boolean lockedAgencies, WhiteSpot whiteSpot, GroupChat groupChat, Boolean consultantBoundedToConsultingType, WelcomeMessage welcomeMessage, Boolean sendFurtherStepsMessage, Boolean sendSaveSessionDataMessage, Boolean isSetEmailAllowed, SessionDataInitializing sessionDataInitializing, Monitoring monitoring, Boolean initializeFeedbackChat, Boolean isPeerChat, Boolean languageFormal, Roles roles, Notifications notifications, Registration registration, Titles titles, Urls urls, Boolean showAskerProfile, Boolean isVideoCallAllowed, Boolean isSubsequentRegistrationAllowed, Boolean isAnonymousConversationAllowed, List<Object> voluntaryComponents, RequiredComponents requiredComponents, WelcomeScreen welcomeScreen) {
        super();
        this.id = id;
        this.tenantId = tenantId;
        this.description = description;
        this.groups = groups;
        this.furtherInformation = furtherInformation;
        this.slug = slug;
        this.excludeNonMainConsultantsFromTeamSessions = excludeNonMainConsultantsFromTeamSessions;
        this.lockedAgencies = lockedAgencies;
        this.whiteSpot = whiteSpot;
        this.groupChat = groupChat;
        this.consultantBoundedToConsultingType = consultantBoundedToConsultingType;
        this.welcomeMessage = welcomeMessage;
        this.sendFurtherStepsMessage = sendFurtherStepsMessage;
        this.sendSaveSessionDataMessage = sendSaveSessionDataMessage;
        this.isSetEmailAllowed = isSetEmailAllowed;
        this.sessionDataInitializing = sessionDataInitializing;
        this.monitoring = monitoring;
        this.initializeFeedbackChat = initializeFeedbackChat;
        this.isPeerChat = isPeerChat;
        this.languageFormal = languageFormal;
        this.roles = roles;
        this.notifications = notifications;
        this.registration = registration;
        this.titles = titles;
        this.urls = urls;
        this.showAskerProfile = showAskerProfile;
        this.isVideoCallAllowed = isVideoCallAllowed;
        this.isSubsequentRegistrationAllowed = isSubsequentRegistrationAllowed;
        this.isAnonymousConversationAllowed = isAnonymousConversationAllowed;
        this.voluntaryComponents = voluntaryComponents;
        this.requiredComponents = requiredComponents;
        this.welcomeScreen = welcomeScreen;
    }

    /**
     * The unique identifier for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * The unique identifier for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public ConsultingType withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * The unique identifier of the tenant that the consulting type belongs to.
     * 
     */
    @JsonProperty("tenantId")
    public Integer getTenantId() {
        return tenantId;
    }

    /**
     * The unique identifier of the tenant that the consulting type belongs to.
     * 
     */
    @JsonProperty("tenantId")
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public ConsultingType withTenantId(Integer tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * Description for the consulting type
     * 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Description for the consulting type
     * 
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public ConsultingType withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Group names - the same group names are combined in the consulting type structure
     * 
     */
    @JsonProperty("groups")
    public List<String> getGroups() {
        return groups;
    }

    /**
     * Group names - the same group names are combined in the consulting type structure
     * 
     */
    @JsonProperty("groups")
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public ConsultingType withGroups(List<String> groups) {
        this.groups = groups;
        return this;
    }

    /**
     * Label and link for further information of the consulting type
     * 
     */
    @JsonProperty("furtherInformation")
    public FurtherInformation getFurtherInformation() {
        return furtherInformation;
    }

    /**
     * Label and link for further information of the consulting type
     * 
     */
    @JsonProperty("furtherInformation")
    public void setFurtherInformation(FurtherInformation furtherInformation) {
        this.furtherInformation = furtherInformation;
    }

    public ConsultingType withFurtherInformation(FurtherInformation furtherInformation) {
        this.furtherInformation = furtherInformation;
        return this;
    }

    /**
     * The unique slug for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    /**
     * The unique slug for the consulting type
     * (Required)
     * 
     */
    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ConsultingType withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    /**
     * True, if only consultants with role main-consultant should be added to all team sessions
     * 
     */
    @JsonProperty("excludeNonMainConsultantsFromTeamSessions")
    public Boolean getExcludeNonMainConsultantsFromTeamSessions() {
        return excludeNonMainConsultantsFromTeamSessions;
    }

    /**
     * True, if only consultants with role main-consultant should be added to all team sessions
     * 
     */
    @JsonProperty("excludeNonMainConsultantsFromTeamSessions")
    public void setExcludeNonMainConsultantsFromTeamSessions(Boolean excludeNonMainConsultantsFromTeamSessions) {
        this.excludeNonMainConsultantsFromTeamSessions = excludeNonMainConsultantsFromTeamSessions;
    }

    public ConsultingType withExcludeNonMainConsultantsFromTeamSessions(Boolean excludeNonMainConsultantsFromTeamSessions) {
        this.excludeNonMainConsultantsFromTeamSessions = excludeNonMainConsultantsFromTeamSessions;
        return this;
    }

    /**
     * If true, no agency with this consulting type can be deleted or set offline via the admin api
     * 
     */
    @JsonProperty("lockedAgencies")
    public Boolean getLockedAgencies() {
        return lockedAgencies;
    }

    /**
     * If true, no agency with this consulting type can be deleted or set offline via the admin api
     * 
     */
    @JsonProperty("lockedAgencies")
    public void setLockedAgencies(Boolean lockedAgencies) {
        this.lockedAgencies = lockedAgencies;
    }

    public ConsultingType withLockedAgencies(Boolean lockedAgencies) {
        this.lockedAgencies = lockedAgencies;
        return this;
    }

    /**
     * Behavior regarding the white spots agencies
     * 
     */
    @JsonProperty("whiteSpot")
    public WhiteSpot getWhiteSpot() {
        return whiteSpot;
    }

    /**
     * Behavior regarding the white spots agencies
     * 
     */
    @JsonProperty("whiteSpot")
    public void setWhiteSpot(WhiteSpot whiteSpot) {
        this.whiteSpot = whiteSpot;
    }

    public ConsultingType withWhiteSpot(WhiteSpot whiteSpot) {
        this.whiteSpot = whiteSpot;
        return this;
    }

    /**
     * Group chat settings
     * 
     */
    @JsonProperty("groupChat")
    public GroupChat getGroupChat() {
        return groupChat;
    }

    /**
     * Group chat settings
     * 
     */
    @JsonProperty("groupChat")
    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }

    public ConsultingType withGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
        return this;
    }

    /**
     * True, if a consultant may only be assigned to agencies with this consulting type
     * 
     */
    @JsonProperty("consultantBoundedToConsultingType")
    public Boolean getConsultantBoundedToConsultingType() {
        return consultantBoundedToConsultingType;
    }

    /**
     * True, if a consultant may only be assigned to agencies with this consulting type
     * 
     */
    @JsonProperty("consultantBoundedToConsultingType")
    public void setConsultantBoundedToConsultingType(Boolean consultantBoundedToConsultingType) {
        this.consultantBoundedToConsultingType = consultantBoundedToConsultingType;
    }

    public ConsultingType withConsultantBoundedToConsultingType(Boolean consultantBoundedToConsultingType) {
        this.consultantBoundedToConsultingType = consultantBoundedToConsultingType;
        return this;
    }

    /**
     * Settings for the welcome message
     * 
     */
    @JsonProperty("welcomeMessage")
    public WelcomeMessage getWelcomeMessage() {
        return welcomeMessage;
    }

    /**
     * Settings for the welcome message
     * 
     */
    @JsonProperty("welcomeMessage")
    public void setWelcomeMessage(WelcomeMessage welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public ConsultingType withWelcomeMessage(WelcomeMessage welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
        return this;
    }

    /**
     * Indicates whether if the system should send an automatic further steps message to all enquiries for this consulting type or not
     * 
     */
    @JsonProperty("sendFurtherStepsMessage")
    public Boolean getSendFurtherStepsMessage() {
        return sendFurtherStepsMessage;
    }

    /**
     * Indicates whether if the system should send an automatic further steps message to all enquiries for this consulting type or not
     * 
     */
    @JsonProperty("sendFurtherStepsMessage")
    public void setSendFurtherStepsMessage(Boolean sendFurtherStepsMessage) {
        this.sendFurtherStepsMessage = sendFurtherStepsMessage;
    }

    public ConsultingType withSendFurtherStepsMessage(Boolean sendFurtherStepsMessage) {
        this.sendFurtherStepsMessage = sendFurtherStepsMessage;
        return this;
    }

    /**
     * Indicates whether if the system should send an automatic message with a prompt for the user to enter optional information to the session
     * 
     */
    @JsonProperty("sendSaveSessionDataMessage")
    public Boolean getSendSaveSessionDataMessage() {
        return sendSaveSessionDataMessage;
    }

    /**
     * Indicates whether if the system should send an automatic message with a prompt for the user to enter optional information to the session
     * 
     */
    @JsonProperty("sendSaveSessionDataMessage")
    public void setSendSaveSessionDataMessage(Boolean sendSaveSessionDataMessage) {
        this.sendSaveSessionDataMessage = sendSaveSessionDataMessage;
    }

    public ConsultingType withSendSaveSessionDataMessage(Boolean sendSaveSessionDataMessage) {
        this.sendSaveSessionDataMessage = sendSaveSessionDataMessage;
        return this;
    }

    /**
     * Asker is allowed to set his email when he is registered for at least one consultingType with this setting 'true'
     * 
     */
    @JsonProperty("isSetEmailAllowed")
    public Boolean getIsSetEmailAllowed() {
        return isSetEmailAllowed;
    }

    /**
     * Asker is allowed to set his email when he is registered for at least one consultingType with this setting 'true'
     * 
     */
    @JsonProperty("isSetEmailAllowed")
    public void setIsSetEmailAllowed(Boolean isSetEmailAllowed) {
        this.isSetEmailAllowed = isSetEmailAllowed;
    }

    public ConsultingType withIsSetEmailAllowed(Boolean isSetEmailAllowed) {
        this.isSetEmailAllowed = isSetEmailAllowed;
        return this;
    }

    /**
     * Settings to control which optional fields for consultation should be initialized for this consultation type.
     * 
     */
    @JsonProperty("sessionDataInitializing")
    public SessionDataInitializing getSessionDataInitializing() {
        return sessionDataInitializing;
    }

    /**
     * Settings to control which optional fields for consultation should be initialized for this consultation type.
     * 
     */
    @JsonProperty("sessionDataInitializing")
    public void setSessionDataInitializing(SessionDataInitializing sessionDataInitializing) {
        this.sessionDataInitializing = sessionDataInitializing;
    }

    public ConsultingType withSessionDataInitializing(SessionDataInitializing sessionDataInitializing) {
        this.sessionDataInitializing = sessionDataInitializing;
        return this;
    }

    /**
     * Settings for the initializing of the monitoring
     * 
     */
    @JsonProperty("monitoring")
    public Monitoring getMonitoring() {
        return monitoring;
    }

    /**
     * Settings for the initializing of the monitoring
     * 
     */
    @JsonProperty("monitoring")
    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    public ConsultingType withMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
        return this;
    }

    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("initializeFeedbackChat")
    public Boolean getInitializeFeedbackChat() {
        return initializeFeedbackChat;
    }

    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("initializeFeedbackChat")
    public void setInitializeFeedbackChat(Boolean initializeFeedbackChat) {
        this.initializeFeedbackChat = initializeFeedbackChat;
    }

    public ConsultingType withInitializeFeedbackChat(Boolean initializeFeedbackChat) {
        this.initializeFeedbackChat = initializeFeedbackChat;
        return this;
    }

    /**
     * Indicates whether the chat should be initialized as peer chat for every session of this consulting type
     * 
     */
    @JsonProperty("isPeerChat")
    public Boolean getIsPeerChat() {
        return isPeerChat;
    }

    /**
     * Indicates whether the chat should be initialized as peer chat for every session of this consulting type
     * 
     */
    @JsonProperty("isPeerChat")
    public void setIsPeerChat(Boolean isPeerChat) {
        this.isPeerChat = isPeerChat;
    }

    public ConsultingType withIsPeerChat(Boolean isPeerChat) {
        this.isPeerChat = isPeerChat;
        return this;
    }

    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("languageFormal")
    public Boolean getLanguageFormal() {
        return languageFormal;
    }

    /**
     * Indicates whether the feedback chat should be initialized for every session of this consulting type
     * 
     */
    @JsonProperty("languageFormal")
    public void setLanguageFormal(Boolean languageFormal) {
        this.languageFormal = languageFormal;
    }

    public ConsultingType withLanguageFormal(Boolean languageFormal) {
        this.languageFormal = languageFormal;
        return this;
    }

    /**
     * Role definitions
     * (Required)
     * 
     */
    @JsonProperty("roles")
    public Roles getRoles() {
        return roles;
    }

    /**
     * Role definitions
     * (Required)
     * 
     */
    @JsonProperty("roles")
    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public ConsultingType withRoles(Roles roles) {
        this.roles = roles;
        return this;
    }

    /**
     * Settings for the mail notifications
     * 
     */
    @JsonProperty("notifications")
    public Notifications getNotifications() {
        return notifications;
    }

    /**
     * Settings for the mail notifications
     * 
     */
    @JsonProperty("notifications")
    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public ConsultingType withNotifications(Notifications notifications) {
        this.notifications = notifications;
        return this;
    }

    /**
     * Settings for the registration process
     * (Required)
     * 
     */
    @JsonProperty("registration")
    public Registration getRegistration() {
        return registration;
    }

    /**
     * Settings for the registration process
     * (Required)
     * 
     */
    @JsonProperty("registration")
    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public ConsultingType withRegistration(Registration registration) {
        this.registration = registration;
        return this;
    }

    /**
     * Titles for this consulting type, which are displayed to the user
     * (Required)
     * 
     */
    @JsonProperty("titles")
    public Titles getTitles() {
        return titles;
    }

    /**
     * Titles for this consulting type, which are displayed to the user
     * (Required)
     * 
     */
    @JsonProperty("titles")
    public void setTitles(Titles titles) {
        this.titles = titles;
    }

    public ConsultingType withTitles(Titles titles) {
        this.titles = titles;
        return this;
    }

    /**
     * Forwarding urls
     * (Required)
     * 
     */
    @JsonProperty("urls")
    public Urls getUrls() {
        return urls;
    }

    /**
     * Forwarding urls
     * (Required)
     * 
     */
    @JsonProperty("urls")
    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public ConsultingType withUrls(Urls urls) {
        this.urls = urls;
        return this;
    }

    /**
     * True to show asker profile including monitoring for consultants. False, when asker profile and monitoring should not be viewable.
     * 
     */
    @JsonProperty("showAskerProfile")
    public Boolean getShowAskerProfile() {
        return showAskerProfile;
    }

    /**
     * True to show asker profile including monitoring for consultants. False, when asker profile and monitoring should not be viewable.
     * 
     */
    @JsonProperty("showAskerProfile")
    public void setShowAskerProfile(Boolean showAskerProfile) {
        this.showAskerProfile = showAskerProfile;
    }

    public ConsultingType withShowAskerProfile(Boolean showAskerProfile) {
        this.showAskerProfile = showAskerProfile;
        return this;
    }

    /**
     * True to enable video call buttons for consultants. False, when video call should be deactivated for consulting type.
     * 
     */
    @JsonProperty("isVideoCallAllowed")
    public Boolean getIsVideoCallAllowed() {
        return isVideoCallAllowed;
    }

    /**
     * True to enable video call buttons for consultants. False, when video call should be deactivated for consulting type.
     * 
     */
    @JsonProperty("isVideoCallAllowed")
    public void setIsVideoCallAllowed(Boolean isVideoCallAllowed) {
        this.isVideoCallAllowed = isVideoCallAllowed;
    }

    public ConsultingType withIsVideoCallAllowed(Boolean isVideoCallAllowed) {
        this.isVideoCallAllowed = isVideoCallAllowed;
        return this;
    }

    /**
     * If true, askers additionally can register for this consulting type on their profile page at a later date.
     * 
     */
    @JsonProperty("isSubsequentRegistrationAllowed")
    public Boolean getIsSubsequentRegistrationAllowed() {
        return isSubsequentRegistrationAllowed;
    }

    /**
     * If true, askers additionally can register for this consulting type on their profile page at a later date.
     * 
     */
    @JsonProperty("isSubsequentRegistrationAllowed")
    public void setIsSubsequentRegistrationAllowed(Boolean isSubsequentRegistrationAllowed) {
        this.isSubsequentRegistrationAllowed = isSubsequentRegistrationAllowed;
    }

    public ConsultingType withIsSubsequentRegistrationAllowed(Boolean isSubsequentRegistrationAllowed) {
        this.isSubsequentRegistrationAllowed = isSubsequentRegistrationAllowed;
        return this;
    }

    /**
     * Indicates whether anonymous 1:1 chats can be performed.
     * 
     */
    @JsonProperty("isAnonymousConversationAllowed")
    public Boolean getIsAnonymousConversationAllowed() {
        return isAnonymousConversationAllowed;
    }

    /**
     * Indicates whether anonymous 1:1 chats can be performed.
     * 
     */
    @JsonProperty("isAnonymousConversationAllowed")
    public void setIsAnonymousConversationAllowed(Boolean isAnonymousConversationAllowed) {
        this.isAnonymousConversationAllowed = isAnonymousConversationAllowed;
    }

    public ConsultingType withIsAnonymousConversationAllowed(Boolean isAnonymousConversationAllowed) {
        this.isAnonymousConversationAllowed = isAnonymousConversationAllowed;
        return this;
    }

    /**
     * Can be provided to display additional fields in the asker profile.
     * 
     */
    @JsonProperty("voluntaryComponents")
    public List<Object> getVoluntaryComponents() {
        return voluntaryComponents;
    }

    /**
     * Can be provided to display additional fields in the asker profile.
     * 
     */
    @JsonProperty("voluntaryComponents")
    public void setVoluntaryComponents(List<Object> voluntaryComponents) {
        this.voluntaryComponents = voluntaryComponents;
    }

    public ConsultingType withVoluntaryComponents(List<Object> voluntaryComponents) {
        this.voluntaryComponents = voluntaryComponents;
        return this;
    }

    @JsonProperty("requiredComponents")
    public RequiredComponents getRequiredComponents() {
        return requiredComponents;
    }

    @JsonProperty("requiredComponents")
    public void setRequiredComponents(RequiredComponents requiredComponents) {
        this.requiredComponents = requiredComponents;
    }

    public ConsultingType withRequiredComponents(RequiredComponents requiredComponents) {
        this.requiredComponents = requiredComponents;
        return this;
    }

    /**
     * Properties for registration welcome screen items
     * 
     */
    @JsonProperty("welcomeScreen")
    public WelcomeScreen getWelcomeScreen() {
        return welcomeScreen;
    }

    /**
     * Properties for registration welcome screen items
     * 
     */
    @JsonProperty("welcomeScreen")
    public void setWelcomeScreen(WelcomeScreen welcomeScreen) {
        this.welcomeScreen = welcomeScreen;
    }

    public ConsultingType withWelcomeScreen(WelcomeScreen welcomeScreen) {
        this.welcomeScreen = welcomeScreen;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(tenantId).append(description).append(groups).append(furtherInformation).append(slug).append(excludeNonMainConsultantsFromTeamSessions).append(lockedAgencies).append(whiteSpot).append(groupChat).append(consultantBoundedToConsultingType).append(welcomeMessage).append(sendFurtherStepsMessage).append(sendSaveSessionDataMessage).append(isSetEmailAllowed).append(sessionDataInitializing).append(monitoring).append(initializeFeedbackChat).append(isPeerChat).append(languageFormal).append(roles).append(notifications).append(registration).append(titles).append(urls).append(showAskerProfile).append(isVideoCallAllowed).append(isSubsequentRegistrationAllowed).append(isAnonymousConversationAllowed).append(voluntaryComponents).append(requiredComponents).append(welcomeScreen).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ConsultingType) == false) {
            return false;
        }
        ConsultingType rhs = ((ConsultingType) other);
        return new EqualsBuilder().append(id, rhs.id).append(tenantId, rhs.tenantId).append(description, rhs.description).append(groups, rhs.groups).append(furtherInformation, rhs.furtherInformation).append(slug, rhs.slug).append(excludeNonMainConsultantsFromTeamSessions, rhs.excludeNonMainConsultantsFromTeamSessions).append(lockedAgencies, rhs.lockedAgencies).append(whiteSpot, rhs.whiteSpot).append(groupChat, rhs.groupChat).append(consultantBoundedToConsultingType, rhs.consultantBoundedToConsultingType).append(welcomeMessage, rhs.welcomeMessage).append(sendFurtherStepsMessage, rhs.sendFurtherStepsMessage).append(sendSaveSessionDataMessage, rhs.sendSaveSessionDataMessage).append(isSetEmailAllowed, rhs.isSetEmailAllowed).append(sessionDataInitializing, rhs.sessionDataInitializing).append(monitoring, rhs.monitoring).append(initializeFeedbackChat, rhs.initializeFeedbackChat).append(isPeerChat, rhs.isPeerChat).append(languageFormal, rhs.languageFormal).append(roles, rhs.roles).append(notifications, rhs.notifications).append(registration, rhs.registration).append(titles, rhs.titles).append(urls, rhs.urls).append(showAskerProfile, rhs.showAskerProfile).append(isVideoCallAllowed, rhs.isVideoCallAllowed).append(isSubsequentRegistrationAllowed, rhs.isSubsequentRegistrationAllowed).append(isAnonymousConversationAllowed, rhs.isAnonymousConversationAllowed).append(voluntaryComponents, rhs.voluntaryComponents).append(requiredComponents, rhs.requiredComponents).append(welcomeScreen, rhs.welcomeScreen).isEquals();
    }

}
