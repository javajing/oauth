package net.aulang.lang.oauth.server.core;

import net.aulang.lang.oauth.document.Account;
import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.document.ThirdParty;
import net.aulang.lang.oauth.exception.AuthException;

public interface AuthService {
    Api getApi();

    Account authenticate(OAuthServer server, String code) throws AuthException;

    ThirdParty bind(OAuthServer server, String code, Account account) throws AuthException;

    boolean supports(OAuthServer server);
}

