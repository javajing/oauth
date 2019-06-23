package net.aulang.lang.oauth.model;

import java.io.Serializable;

public class AccessToken implements Serializable {
    private static final long serialVersionUID = 1L;

    private String access_token;
    private String refresh_token;
    private int expires_in;

    public AccessToken() {

    }

    public AccessToken(String access_token, String refresh_token, int expires_in) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public static AccessToken create(String access_token, String refresh_token, int expires_in) {
        return new AccessToken(access_token, refresh_token, expires_in);
    }
}