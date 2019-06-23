package net.aulang.lang.oauth.restcontroller;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.AccountToken;
import net.aulang.lang.oauth.model.Profile;
import net.aulang.lang.oauth.manage.AccountBiz;
import net.aulang.lang.oauth.manage.AccountTokenBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileRestController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    @GetMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> me(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.substring(Constants.BEARER.length()).trim();
        AccountToken accountToken = accountTokenBiz.findByAccessToken(accessToken);
        if (accountToken != null) {
            String accountId = accountToken.getAccountId();
            Profile user = accountBiz.getUser(accountId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("账号不存在"));
            }
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("无效的access_token"));
        }
    }
}
