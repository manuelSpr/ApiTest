package support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("login")
public class User {
    @JsonProperty("grant_type")
    private Object grantType;
    @JsonProperty("userName")
    private Object userName;
    @JsonProperty("password")
    private Object password;

    /**
     *
     */
    public User() {
        super();
    }

    /**
     * @param userName
     * @param password
     * @param grantType
     */
    public User(String userName, String password, String grantType) {
        super();
        this.userName = userName;
        this.password = password;
        this.grantType = grantType;
    }

    /**
     * @return the userName
     */
    public Object getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public Object getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the grantType
     */
    public Object getGrantType() {
        return grantType;
    }

    /**
     * @param grantType the grantType to set
     */
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
