package config;
import support.ConstantData;

public class Configuration {
    private String platform;
    private String env;
    private String localTesting;
    private String buildId;
    private boolean auth;
    private String authResource;
    private String authUser;
    private String authPsw;
    private boolean sendEmail;
    private String emailFrom;
    private String emailPsw;
    private String[] emailTo;
    private String type;
    private String webServiceVersion;
    private String databaseHost;
    private String splunkHost;
    private int splunkTimeout;
    private String inventoryHost;
    private String jamaHost;
    private String versions;
    private String htmlReportFileName;

    /**
     * @returns the HTML File report name
     */
    public String getHtmlReportFileName() {
        return (htmlReportFileName.equals("") ? ConstantData.DEFAULT_HTML_REPORT_FILENAME : (!htmlReportFileName.contains(ConstantData.DEFAULT_HTML_FILE_EXTENSION)) ? htmlReportFileName.concat(ConstantData.DEFAULT_HTML_FILE_EXTENSION) : htmlReportFileName);
    }

    /**
     * @param htmlReportFileName field from the configuration file
     */
    public void setHtmlReportFileName(String htmlReportFileName) {
        this.htmlReportFileName = htmlReportFileName;
    }


    /**
     * @returns Splunk Timeout value from configuration file
     */
    public int getSplunkTimeout() {
        return (splunkTimeout <= 0) ? 5 : splunkTimeout;
    }

    /**
     * @param splunkTimeout Sets the splunk Timeout variable from the config file
     */
    public void setSplunkTimeout(int splunkTimeout) {
        this.splunkTimeout = splunkTimeout;
    }

    /**
     * @returns desired versions to run within the automation
     */
    public String getVersions() {
        return versions;
    }

    /**
     *
     * @param versions  versions key value from the config file
     */
    public void setVersions(String versions) {
        this.versions = versions;
    }


    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the env
     */
    public String getEnv() {
        return env;
    }

    /**
     * @param env the env to set
     */
    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * @return the localTesting
     */
    public String getLocalTesting() {
        return localTesting;
    }

    /**
     * @param localTesting the localTesting to set
     */
    public void setLocalTesting(String localTesting) {
        this.localTesting = localTesting;
    }

    /**
     * @return the buildId
     */
    public String getBuildId() {
        return buildId;
    }

    /**
     * @param buildId the buildId to set
     */
    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    /**
     *
     * @return the auth
     */
    public boolean isAuth() {
        return auth;
    }

    /**
     *
     * @param auth
     */
    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    /**
     *
     * @return
     */
    public String getAuthResource() {
        return authResource;
    }

    /**
     *
     * @param authResource
     */
    public void setAuthResource(String authResource) {
        this.authResource = authResource;
    }

    /**
     * @return the authUser
     */
    public String getAuthUser() {
        return authUser;
    }

    /**
     * @param authUser the authUser to set
     */
    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    /**
     * @return the authPsw
     */
    public String getAuthPsw() {
        return authPsw;
    }

    /**
     * @param authPsw the authPsw to set
     */
    public void setAuthPsw(String authPsw) {
        this.authPsw = authPsw;
    }

    /**
     *
     * @return sendEmail
     */
    public boolean isSendEmail() {
        return sendEmail;
    }

    /**
     *
     * @param sendEmail
     */
    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    /**
     *
     * @return emailFrom
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     *
     * @param emailFrom
     */
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     *
     * @return emailPsw
     */
    public String getEmailPsw() {
        return emailPsw;
    }

    /**
     *
     * @param emailPsw
     */
    public void setEmailPsw(String emailPsw) {
        this.emailPsw = emailPsw;
    }

    /**
     *
     * @return emailTo
     */
    public String[] getEmailTo() {
        return emailTo;
    }

    /**
     *
     * @param emailTo
     */
    public void setEmailTo(String[] emailTo) {
        this.emailTo = emailTo;
    }

    /**
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getWebServiceVersion() {
        return webServiceVersion;
    }

    /**
     *
     * @param webServiceVersion
     */
    public void setWebServiceVersion(String webServiceVersion) {
        this.webServiceVersion = webServiceVersion;
    }

    /**
     *
     * @return database host URL
     */
    public String getDatabaseHost() {
        return databaseHost;
    }

    /**
     *
     * @param databaseHost
     */
    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    /**
     *
     * @return splunk host URL
     */
    public String getSplunkHost() {
        return splunkHost;
    }

    /**
     *
     * @param splunkHost
     */

    public void setSplunkHost(String splunkHost) {
        this.splunkHost = splunkHost;
    }

    /**
     *
     * @return inventory WS host URL
     */
    public String getInventoryHost() {
        return inventoryHost;
    }

    /**
     *
     * @param inventoryHost
     */

    public void setInventoryHost(String inventoryHost) {
        this.inventoryHost = inventoryHost;
    }

    /**
     *
     * @return
     */
    public String getJamaHost() {
        return jamaHost;
    }

    /**
     *
     * @param jamaHost
     */
    public void setJamaHost(String jamaHost) {
        this.jamaHost = jamaHost;
    }
}
