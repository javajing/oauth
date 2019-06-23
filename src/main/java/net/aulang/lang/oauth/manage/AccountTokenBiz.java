package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.AccountToken;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.document.OAuthCode;
import net.aulang.lang.oauth.repository.AccountTokenRepository;
import net.aulang.lang.oauth.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Service
public class AccountTokenBiz {
    @Autowired
    private AccountTokenRepository dao;
    @Autowired
    private OAuthClientBiz clientBiz;

    public AccountToken save(AccountToken token) {
        AccountToken accountToken = findByAccountIdAndClientIdAndRedirectUrl(
                token.getAccountId(),
                token.getClientId(),
                token.getRedirectUrl()
        );

        if (accountToken != null) {
            token.setId(accountToken.getId());
        }

        return dao.save(token);
    }

    public AccountToken findByAccessToken(String accessToken) {
        return dao.findByAccessToken(accessToken);
    }

    public AccountToken findByAccountIdAndClientIdAndRedirectUrl(String accountId, String clientId, String redirectUrl) {
        return dao.findByAccountIdAndClientIdAndRedirectUrl(accountId, clientId, redirectUrl);
    }

    public AccountToken refreshAccessToken(String refreshToken) {
        try {
            return refreshAccessToken(refreshToken, IdUtils.UUID());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountToken refreshAccessToken(String refreshToken, String newAccessToken) {
        AccountToken accountToken = dao.findByRefreshToken(refreshToken);

        if (accountToken == null) {
            return null;
        }

        Date refreshTokenExpiration = accountToken.getRefreshTokenExpiresAt();
        if (refreshTokenExpiration != null && refreshTokenExpiration.before(new Date())) {
            throw new RuntimeException("令牌已过期");
        }

        accountToken.setAccessToken(newAccessToken);

        OAuthClient client = clientBiz.findByClientId(accountToken.getClientId());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, client.getAccessTokenValiditySeconds());
        accountToken.setAccessTokenExpiresAt(calendar.getTime());

        calendar.add(Calendar.SECOND, client.getRefreshTokenValiditySeconds());
        accountToken.setRefreshTokenExpiresAt(calendar.getTime());

        return save(accountToken);
    }

    public AccountToken create(
            String clientId,
            Set<String> scopes,
            String redirectUrl,
            String accountId) {
        String accessToken = IdUtils.UUID();
        String refreshToken = IdUtils.UUID();
        return create(accessToken, refreshToken, clientId, scopes, redirectUrl, accountId);
    }

    public AccountToken create(
            String accessToken,
            String refreshToken,
            String clientId,
            Set<String> scopes,
            String redirectUrl,
            String accountId) {
        AccountToken accountToken = new AccountToken();

        accountToken.setScopes(scopes);
        accountToken.setClientId(clientId);
        accountToken.setRedirectUrl(redirectUrl);
        accountToken.setAccountId(accountId);
        accountToken.setAccessToken(accessToken);
        accountToken.setRefreshToken(refreshToken);


        OAuthClient client = clientBiz.findByClientId(clientId);
        if (client == null) {
            throw new RuntimeException("客户端不存在");
        }

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, client.getAccessTokenValiditySeconds());
        accountToken.setAccessTokenExpiresAt(calendar.getTime());

        calendar.add(Calendar.SECOND, client.getRefreshTokenValiditySeconds());
        accountToken.setRefreshTokenExpiresAt(calendar.getTime());

        return save(accountToken);
    }

    public AccountToken createByCode(OAuthCode code) {
        String accessToken = IdUtils.UUID();
        String refreshToken = IdUtils.UUID();
        return create(
                accessToken,
                refreshToken,
                code.getClientId(),
                code.getScopes(),
                code.getRedirectUrl(),
                code.getAccountId()
        );
    }
}
