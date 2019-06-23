package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.common.OAuthConstants;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.repository.AuthorizeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthorizeRequestBiz {
    @Autowired
    private OAuthCodeBiz oAuthCodeBiz;
    @Autowired
    private OAuthClientBiz oAuthClientBiz;
    @Autowired
    private AuthorizeRequestRepository dao;
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    public AuthorizeRequest save(AuthorizeRequest entity) {
        return dao.save(entity);
    }

    public AuthorizeRequest create(String accountId,
                                   String clientId,
                                   String responseType,
                                   String redirectUrl,
                                   Set<String> scopes,
                                   String state) {

        AuthorizeRequest request = new AuthorizeRequest();

        request.setAccountId(accountId);
        request.setAuthenticated(true);

        request.setClientId(clientId);
        request.setResponseType(responseType);
        request.setRedirectUrl(redirectUrl);
        request.setScopes(scopes);
        request.setState(state);

        return request;
    }

    public AuthorizeRequest create(String clientId,
                                   String responseType,
                                   String redirectUrl,
                                   Set<String> scopes,
                                   String state) {

        AuthorizeRequest request = new AuthorizeRequest();

        request.setClientId(clientId);
        request.setResponseType(responseType);
        request.setRedirectUrl(redirectUrl);
        request.setScopes(scopes);
        request.setState(state);

        return dao.save(request);
    }

    public AuthorizeRequest findOne(String id) {
        Optional<AuthorizeRequest> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public String redirect(AuthorizeRequest request, HttpServletResponse response) {
        String state = request.getState();
        StringBuilder redirectUrl = new StringBuilder(request.getRedirectUrl());
        if (OAuthConstants.GrantType.IMPLICIT.equalsIgnoreCase(request.getResponseType())) {
            OAuthClient client = oAuthClientBiz.findByClientId(request.getClientId());
            String accessToken = accountTokenBiz.create(
                    request.getClientId(),
                    request.getScopes(),
                    request.getRedirectUrl(),
                    request.getAccountId()
            ).getAccessToken();
            /**
             * 简化模式: #access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN&state=STATE
             */
            long expires_in = client.getAccessTokenValiditySeconds();
            redirectUrl
                    .append(Constants.QUESTIONMARK)
                    .append(OAuthConstants.ACCESS_TOKEN).append(Constants.EQUALMARK).append(accessToken)
                    .append(Constants.ANDMARK)
                    .append(OAuthConstants.EXPIRES_IN).append(Constants.EQUALMARK).append(expires_in);
            if (state != null) {
                redirectUrl
                        .append(Constants.ANDMARK)
                        .append(OAuthConstants.STATE).append(Constants.EQUALMARK).append(state);
            }
            if (response != null && !response.isCommitted()) {
                response.addCookie(Constants.ssoCookie(accessToken));
            }
        } else {
            String code = oAuthCodeBiz.create(
                    request.getClientId(),
                    request.getScopes(),
                    request.getRedirectUrl(),
                    request.getAccountId()
            ).getId();
            /**
             * 授权码模式：?code=CODE&state=STATE
             */
            if (redirectUrl.lastIndexOf(Constants.QUESTIONMARK) == -1) {
                redirectUrl.append(Constants.QUESTIONMARK);
            }
            redirectUrl.append(OAuthConstants.CODE).append(Constants.EQUALMARK).append(code);
            if (state != null) {
                redirectUrl
                        .append(Constants.ANDMARK)
                        .append(OAuthConstants.STATE).append(Constants.EQUALMARK).append(state);
            }

        }
        return redirectUrl.toString();
    }
}
