package net.aulang.lang.oauth.controller;

import net.aulang.lang.oauth.authentication.AuthServiceProvider;
import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.*;
import net.aulang.lang.oauth.exception.AuthException;
import net.aulang.lang.oauth.manage.*;
import net.aulang.lang.oauth.server.core.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ThirdLoginController {
    @Autowired
    private OAuthStateBiz stateBiz;
    @Autowired
    private OAuthServerBiz serverBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;
    @Autowired
    private AuthServiceProvider provider;
    @Autowired
    private AuthorizeRequestBiz requestBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    public String error(Model model, String msg) {
        model.addAttribute("error", msg);
        return "error";
    }

    @GetMapping("/third_login/{authorizeId}/{serverId}")
    public String redirectAuthorizeUrl(@PathVariable String authorizeId,
                                       @PathVariable String serverId,
                                       Model model) {
        OAuthServer server = serverBiz.findOne(serverId);
        if (server == null) {
            return error(model, "OAuthServer不存在");
        }
        return "redirect:" + serverBiz.buildAuthorizeUrl(authorizeId, server, null);
    }

    @GetMapping("/third_login")
    public String otherLogin(@RequestParam(name = "code") String code,
                             @RequestParam(name = "state") String state,
                             HttpServletResponse response, Model model) {
        OAuthState authState = stateBiz.findByState(state);
        if (authState == null) {
            return error(model, "请求已超期失效");
        }

        OAuthServer server = serverBiz.findOne(authState.getoAuthServerId());
        if (server == null) {
            return error(model, "无效的第三方服务");
        }

        AuthService authService = provider.get(server);
        if (authService == null) {
            return error(model, "没有第三方服务提供者");
        }

        String authorizeId = authState.getAuthorizeId();
        if (Constants.BIND_STATE_AUTHORIZE_ID.equals(authorizeId)) {
            /**
             * 账号绑定
             */
            try {
                Account account = new Account(authState.getAccountId());
                authService.bind(server, code, account);
                model.addAttribute("name", server.getName());
                return "bind_success";
            } catch (AuthException e) {
                e.printStackTrace();
                return error(model, e.getMessage());
            }
        }

        AuthorizeRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return error(model, "请求已超期失效");
        }

        try {
            Account account = authService.authenticate(server, code);
            request.setAuthenticated(true);
            request.setAccountId(account.getId());
            request = requestBiz.save(request);

            return returnPageBiz.approvalPage(request, response, model);
        } catch (AuthException e) {
            e.printStackTrace();
            return error(model, e.getMessage());
        }
    }

    @GetMapping("/third_login/bind/{name}")
    public String bind(@PathVariable String name,
                       @RequestParam("access_token") String accessToken,
                       Model model) {
        OAuthServer server = serverBiz.findByName(name);
        if (server == null) {
            return error(model, "OAuthServer不存在");
        }

        AccountToken accountToken = accountTokenBiz.findByAccessToken(accessToken);
        if (accountToken == null) {
            return error(model, "登录信息无效");
        }

        return "redirect:" + serverBiz.buildAuthorizeUrl(
                Constants.BIND_STATE_AUTHORIZE_ID,
                server,
                accountToken.getAccountId()
        );
    }
}
