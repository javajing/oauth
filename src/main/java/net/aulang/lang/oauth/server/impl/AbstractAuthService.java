package net.aulang.lang.oauth.server.impl;

import net.aulang.lang.oauth.document.Account;
import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.document.ThirdParty;
import net.aulang.lang.oauth.exception.AuthException;
import net.aulang.lang.oauth.manage.ThirdPartyBiz;
import net.aulang.lang.oauth.server.core.AccessToken;
import net.aulang.lang.oauth.server.core.AuthService;
import net.aulang.lang.oauth.server.core.Profile;

public abstract class AbstractAuthService implements AuthService {
    @Override
    public Account authenticate(OAuthServer server, String code) throws AuthException {
        try {
            AccessToken accessToken = getApi().getAccessToken(server, code);
            Profile profile = getApi().getProfile(server, accessToken);

            ThirdParty thirdParty = getThirdPartyBiz().getAccount(profile);
            if (thirdParty != null) {
                return new Account(thirdParty.getAccountId());
            } else {
                getApi().getDetail(server, accessToken, profile);
                return getThirdPartyBiz().register(profile);
            }
        } catch (Exception e) {
            throw new AuthException(server.getName() + "认证失败", e);
        }
    }

    @Override
    public ThirdParty bind(OAuthServer server, String code, Account account) throws AuthException {
        try {
            AccessToken accessToken = getApi().getAccessToken(server, code);
            Profile profile = getApi().getProfile(server, accessToken);
            return getThirdPartyBiz().bind(account, profile);
        } catch (Exception e) {
            throw new AuthException(server.getName() + "绑定失败", e);
        }
    }

    public abstract ThirdPartyBiz getThirdPartyBiz();
}
