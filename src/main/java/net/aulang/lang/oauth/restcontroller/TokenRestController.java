package net.aulang.lang.oauth.restcontroller;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.AccountToken;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.manage.AccountTokenBiz;
import net.aulang.lang.oauth.manage.AuthorizeRequestBiz;
import net.aulang.lang.oauth.manage.OAuthClientBiz;
import net.aulang.lang.oauth.model.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenRestController {
    @Autowired
    private OAuthClientBiz clientBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;
    @Autowired
    private AuthorizeRequestBiz authorizeRequestBiz;

    @PostMapping(path = "/api/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> captcha(@RequestParam(name = "authorize_id") String authorizeId,
                                          @RequestParam(name = "mobile") String mobile,
                                          @RequestParam(name = "captcha") String captcha) {
        AuthorizeRequest request = authorizeRequestBiz.findOne(authorizeId);
        if (request == null || request.getAccountId() == null) {
            return ResponseEntity.badRequest().body(Constants.error("验证码已失效"));
        }

        if (request.getMobile() == null || !request.getMobile().equals(mobile)) {
            return ResponseEntity.badRequest().body(Constants.error("手机号码不匹配"));
        }

        if (request.getCaptcha() == null || !request.getCaptcha().equals(captcha)) {
            return ResponseEntity.badRequest().body(Constants.error("验证码错误"));
        }

        request.setAuthenticated(true);
        authorizeRequestBiz.save(request);

        String clientId = request.getClientId();
        OAuthClient client = clientBiz.findByClientId(clientId);
        if (client == null) {
            ResponseEntity.badRequest().body(Constants.error("无效的客户端"));
        }

        AccountToken accountToken = accountTokenBiz.create(
                request.getClientId(),
                client.getAutoApproveScopes(),
                request.getRedirectUrl(),
                request.getAccountId()
        );

        return ResponseEntity.ok(
                AccessToken.create(
                        accountToken.getAccessToken(),
                        accountToken.getRefreshToken(),
                        client.getAccessTokenValiditySeconds()
                )
        );
    }
}
