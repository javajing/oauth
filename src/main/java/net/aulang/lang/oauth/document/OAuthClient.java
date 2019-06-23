package net.aulang.lang.oauth.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Document
public class OAuthClient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Indexed(unique = true, sparse = true)
    private String clientId;
    private String clientName;
    private String clientSecret;
    private Map<String, String> scopes = new HashMap<>();
    private Set<String> autoApproveScopes = new HashSet<>();
    private Set<String> authorizedGrantTypes = new HashSet<>();
    private Set<String> registeredRedirectUrl = new HashSet<>();
    private int accessTokenValiditySeconds = 2592000;
    private int refreshTokenValiditySeconds = 7776000;
    private int approvalValiditySeconds = 2592000;
    private String logoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Map<String, String> getScopes() {
        return scopes;
    }

    public void setScopes(Map<String, String> scopes) {
        if (scopes != null) {
            this.scopes = scopes;
        }
    }

    public Set<String> getAutoApproveScopes() {
        return autoApproveScopes;
    }

    public void setAutoApproveScopes(Set<String> autoApproveScopes) {
        if (autoApproveScopes != null) {
            this.autoApproveScopes = autoApproveScopes;
        }
    }

    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
        if (authorizedGrantTypes != null) {
            this.authorizedGrantTypes = authorizedGrantTypes;
        }
    }

    public Set<String> getRegisteredRedirectUrl() {
        return registeredRedirectUrl;
    }

    public void setRegisteredRedirectUrl(Set<String> registeredRedirectUrl) {
        if (registeredRedirectUrl != null) {
            this.registeredRedirectUrl = registeredRedirectUrl;
        }
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public int getApprovalValiditySeconds() {
        return approvalValiditySeconds;
    }

    public void setApprovalValiditySeconds(int approvalValiditySeconds) {
        this.approvalValiditySeconds = approvalValiditySeconds;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}