package net.aulang.lang.oauth.document;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.common.OAuthConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Document
public class OAuthServer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Indexed(unique = true, sparse = true)
    private String name;
    private String logoUrl;
    private boolean enabled = true;
    private String authorizeUrl = "";
    private String accessTokenUrl = "";
    private String profileUrl = "";

    private Map<String, String> authorizeParams = new HashMap<>();
    /**
     * post、get
     */
    private String accessTokenMethod = Constants.POST;
    private Map<String, String> accessTokenParams = new HashMap<>();
    /**
     * json、text
     */
    private String accessTokenType = Constants.JSON;
    private String accessTokenKey = OAuthConstants.ACCESS_TOKEN;
    private String refreshTokenKey = OAuthConstants.REFRESH_TOKEN;
    private String expiresInKey = OAuthConstants.EXPIRES_IN;

    /**
     * post、get、header
     */
    private String profileMethod = Constants.HEADER;
    /**
     * header时有效
     */
    private String profileAuthorization = Constants.BEARER;
    /**
     * post和get时有效
     */
    private Map<String, String> profileParams = new HashMap<>();

    private int sort = 1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Map<String, String> getAuthorizeParams() {
        return authorizeParams;
    }

    public void setAuthorizeParams(Map<String, String> authorizeParams) {
        this.authorizeParams = authorizeParams;
    }

    public String getAccessTokenMethod() {
        return accessTokenMethod;
    }

    public void setAccessTokenMethod(String accessTokenMethod) {
        this.accessTokenMethod = accessTokenMethod;
    }

    public Map<String, String> getAccessTokenParams() {
        return accessTokenParams;
    }

    public void setAccessTokenParams(Map<String, String> accessTokenParams) {
        this.accessTokenParams = accessTokenParams;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public String getRefreshTokenKey() {
        return refreshTokenKey;
    }

    public void setRefreshTokenKey(String refreshTokenKey) {
        this.refreshTokenKey = refreshTokenKey;
    }

    public String getExpiresInKey() {
        return expiresInKey;
    }

    public void setExpiresInKey(String expiresInKey) {
        this.expiresInKey = expiresInKey;
    }

    public String getProfileMethod() {
        return profileMethod;
    }

    public void setProfileMethod(String profileMethod) {
        this.profileMethod = profileMethod;
    }

    public String getProfileAuthorization() {
        return profileAuthorization;
    }

    public void setProfileAuthorization(String profileAuthorization) {
        this.profileAuthorization = profileAuthorization;
    }

    public Map<String, String> getProfileParams() {
        return profileParams;
    }

    public void setProfileParams(Map<String, String> profileParams) {
        this.profileParams = profileParams;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}