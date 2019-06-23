package net.aulang.lang.oauth.controller;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.common.OAuthConstants;
import net.aulang.lang.oauth.document.AccountToken;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.document.OAuthCode;
import net.aulang.lang.oauth.exception.PasswordExpiredException;
import net.aulang.lang.oauth.manage.*;
import net.aulang.lang.oauth.model.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Controller
public class OAuthController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private OAuthClientBiz clientBiz;
    @Autowired
    private OAuthCodeBiz oAuthCodeBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;
    @Autowired
    private ApprovalScopeBiz approvalScopeBiz;
    @Autowired
    private AuthorizeRequestBiz authorizeRequestBiz;

    /**
     * @param clientId     客户端的ID，必选项
     * @param responseType 授权类型，必选项，授权码模式（authorization_code）为"code"，简化模式（implicit）为"token"
     * @param redirectUrl  重定向URL，可选项
     * @param scope        申请的权限范围，可选项
     * @param state        客户端的当前状态，可选项，原样返回该值
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam(name = "client_id") String clientId,
                            @RequestParam(name = "response_type") String responseType,
                            @RequestParam(name = "redirect_url", required = false) String redirectUrl,
                            @RequestParam(name = "scope", required = false) String scope,
                            @RequestParam(name = "state", required = false) String state,
                            @CookieValue(name = Constants.SSO_COOKIE_NAME, required = false) String accessToken,
                            HttpServletResponse response, Model model) {
        OAuthClient client = clientBiz.findByClientId(clientId);
        if (client == null) {
            return Constants.errorPage(model, "无效的client_id");
        }

        String grantType = OAuthConstants.GrantType.responseType(responseType);
        if (grantType == null) {
            return Constants.errorPage(model, "无效的response_type");
        }

        if (!client.getAuthorizedGrantTypes().contains(grantType)) {
            return Constants.errorPage(model, "未授权的response_type");
        }

        String registeredUrl = redirectUrl;
        Set<String> registeredUrls = client.getRegisteredRedirectUrl();
        if (redirectUrl != null) {
            boolean result = registeredUrls.parallelStream().anyMatch(url -> {
                Pattern pattern = Pattern.compile(url, Pattern.CASE_INSENSITIVE);
                return pattern.matcher(redirectUrl).matches();
            });
            if (!result) {
                return Constants.errorPage(model, "未注册的redirectUrl");
            }
        } else {
            if (registeredUrls.size() == 1) {
                registeredUrl = registeredUrls.iterator().next();
            } else {
                return Constants.errorPage(model, "缺失redirectUrl");
            }
        }

        Set<String> scopes = new HashSet<>();
        if (scope != null) {
            List<String> requestScopes = Arrays.asList(scope.split(","));
            if (!client.getScopes().keySet().containsAll(requestScopes)) {
                return Constants.errorPage(model, "无效的scope");
            }
            scopes.addAll(requestScopes);
        } else {
            scopes.addAll(client.getAutoApproveScopes());
        }

        AuthorizeRequest request;
        if (accessToken != null) {
            /**
             * Cookie单点登录
             */
            AccountToken accountToken = accountTokenBiz.findByAccessToken(accessToken);
            if (accountToken != null) {
                String accountId = accountToken.getAccountId();
                request = authorizeRequestBiz.create(accountId, clientId, responseType, registeredUrl, scopes, state);
                return returnPageBiz.grantToken(request, response, model);
            }
        }
        /**
         * 保存登录信息
         */
        request = authorizeRequestBiz.create(clientId, responseType, registeredUrl, scopes, state);
        return returnPageBiz.loginPage(request, client, model);
    }

    @PostMapping("/approval")
    public String approval(@RequestParam(name = "authorize_id") String authorizeId,
                           @RequestParam(name = "scopes", required = false) String[] scopes,
                           @RequestParam(name = "authorized", defaultValue = "true") boolean authorized,
                           HttpServletResponse response, Model model) {
        AuthorizeRequest request = authorizeRequestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        OAuthClient client = clientBiz.findByClientId(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的客户端");
        }

        if (authorized) {
            approvalScopeBiz.save(client, request.getAccountId(), scopes);
            return returnPageBiz.grantToken(request, response, model);
        } else {
            return returnPageBiz.rejectToken(request);
        }
    }

    /**
     * @param clientId     客户端的ID，必选项
     * @param grantType    使用的授权模式，必选项，授权码模式为"authorization_code",密码模式为"password"
     * @param code         授权码模式获得的授权码，授权码模式必选项
     * @param redirectUrl  重定向URI，授权码模式可选项
     * @param username     用户名，必密码模式选项
     * @param password     用户密码，密码模式必选项
     * @param refreshToken 刷新access_token
     */
    @ResponseBody
    @PostMapping(path = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> token(@RequestParam(name = "client_id") String clientId,
                                        @RequestParam(name = "grant_type") String grantType,

                                        @RequestParam(name = "code", required = false) String code,
                                        @RequestParam(name = "client_secret", required = false) String clientSecret,
                                        @RequestParam(name = "redirect_url", required = false) String redirectUrl,

                                        @RequestParam(name = "username", required = false) String username,
                                        @RequestParam(name = "password", required = false) String password,

                                        @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                        HttpServletResponse response) {
        OAuthClient client = clientBiz.findByClientId(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error("无效的客户端"));
        }
        if (!client.getAuthorizedGrantTypes().contains(grantType)) {
            return ResponseEntity.badRequest().body(Constants.error("未授权的grantType"));
        }

        if (OAuthConstants.GrantType.PASSWORD.equalsIgnoreCase(grantType)) {
            /**
             * 密码模式
             */
            if (StringUtils.isAnyBlank(username, password)) {
                return ResponseEntity.badRequest().body(Constants.error("账号和密码不能为空"));
            }

            try {
                String accountId = accountBiz.login(username, password);
                if (accountId != null) {
                    Set<String> scopes = client.getAutoApproveScopes();
                    AccountToken accountToken = accountTokenBiz.create(clientId, scopes, grantType, accountId);

                    response.addCookie(Constants.ssoCookie(accountToken.getAccessToken()));

                    return ResponseEntity.ok(
                            AccessToken.create(
                                    accountToken.getAccessToken(),
                                    accountToken.getRefreshToken(),
                                    client.getAccessTokenValiditySeconds()
                            )
                    );
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.error("账号或密码错误"));
                }
            } catch (AccountLockedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("账号被锁定，请申诉解锁"));
            } catch (PasswordExpiredException e) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Constants.error("密码过期，必须修改密码"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.error("账号或密码错误"));
            }
        } else if (OAuthConstants.GrantType.AUTHORIZATION_CODE.equalsIgnoreCase(grantType)) {
            /**
             * 授权码模式
             */
            if (StringUtils.isBlank(code)) {
                return ResponseEntity.badRequest().body(Constants.error("code不能为空"));
            }
            if (!client.getClientSecret().equals(clientSecret)) {
                return ResponseEntity.badRequest().body(Constants.error("client_secret错误"));
            }
            OAuthCode authCode = oAuthCodeBiz.consumeCode(code);
            if (authCode == null) {
                return ResponseEntity.badRequest().body(Constants.error("无效code"));
            }

            if (!authCode.getRedirectUrl().equalsIgnoreCase(redirectUrl)) {
                return ResponseEntity.badRequest().body(Constants.error("redirect_url不匹配"));
            }
            AccountToken accountToken = accountTokenBiz.createByCode(authCode);

            response.addCookie(Constants.ssoCookie(accountToken.getAccessToken()));

            return ResponseEntity.ok(
                    AccessToken.create(
                            accountToken.getAccessToken(),
                            accountToken.getRefreshToken(),
                            client.getAccessTokenValiditySeconds()
                    )
            );
        } else if (OAuthConstants.GrantType.REFRESH_TOKEN.equalsIgnoreCase(grantType)) {
            /**
             * 刷新access_token
             */
            if (StringUtils.isBlank(refreshToken)) {
                return ResponseEntity.badRequest().body(Constants.error("refresh_token不能为空"));
            }

            AccountToken accountToken = accountTokenBiz.refreshAccessToken(refreshToken);
            if (accountToken != null) {
                return ResponseEntity.ok(
                        AccessToken.create(
                                accountToken.getAccessToken(),
                                accountToken.getRefreshToken(),
                                client.getAccessTokenValiditySeconds()
                        )
                );
            } else {
                return ResponseEntity.badRequest().body(Constants.error("无效的refresh_token"));
            }
        } else {
            return ResponseEntity.badRequest().body(Constants.error("无效的grantType"));
        }
    }
}