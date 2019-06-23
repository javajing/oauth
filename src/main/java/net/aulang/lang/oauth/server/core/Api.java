package net.aulang.lang.oauth.server.core;

import net.aulang.lang.oauth.document.OAuthServer;

public interface Api<T extends Profile> {
    AccessToken getAccessToken(OAuthServer server, String code) throws Exception;

    T getProfile(OAuthServer server, AccessToken accessToken) throws Exception;

    void getDetail(OAuthServer server, AccessToken accessToken, T t);
}
