package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.ApprovalScope;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.document.OAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Service
public class ReturnPageBiz {
    @Autowired
    private OAuthClientBiz clientBiz;
    @Autowired
    private OAuthServerBiz serverBiz;
    @Autowired
    private ApprovalScopeBiz approvalScopeBiz;
    @Autowired
    private AuthorizeRequestBiz authorizeRequestBiz;

    public String rejectToken(AuthorizeRequest request) {
        String redirectUrl = request.getRedirectUrl();
        if (redirectUrl.lastIndexOf(Constants.QUESTIONMARK) == -1) {
            redirectUrl += Constants.QUESTIONMARK;
        } else {
            redirectUrl += Constants.ANDMARK;
        }
        return "redirect:" + redirectUrl + "error=reject";
    }

    public String grantToken(AuthorizeRequest request, HttpServletResponse response, Model model) {
        if (!request.isAuthenticated()) {
            return loginPage(request, null, model);
        }
        return "redirect:" + authorizeRequestBiz.redirect(request, response);
    }

    public String loginPage(AuthorizeRequest request, OAuthClient client, Model model) {
        String authorizeId = request.getId();

        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("captcha", request.getTriedTimes() > 2);

        if (client == null) {
            client = clientBiz.findByClientId(request.getClientId());
        }

        model.addAttribute("servers", serverBiz.getAllServers());
        model.addAttribute("clientName", client.getClientName());

        return "login";
    }

    public String approvalPage(AuthorizeRequest request, HttpServletResponse response, Model model) {
        String authorizeId = request.getId();

        if (!request.isAuthenticated()) {
            return loginPage(request, null, model);
        }

        OAuthClient client = clientBiz.findByClientId(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的客户端");
        }

        Set<String> requestScopes = request.getScopes();
        if (!CollectionUtils.isEmpty(requestScopes)) {
            /**
             * 自动授权
             */
            if (client.getAutoApproveScopes() != null
                    && client.getAutoApproveScopes().containsAll(requestScopes)) {
                return grantToken(request, response, model);
            }
            /**
             * 用户已授权
             */
            ApprovalScope approvalScope = approvalScopeBiz.findByAccountIdAndClientId(
                    request.getAccountId(),
                    client.getClientId()
            );
            if (approvalScope != null
                    && approvalScope.getApproved() != null
                    && approvalScope.getApproved().containsAll(requestScopes)) {
                return grantToken(request, response, model);
            }
        } else {
            /**
             * 没有请求权限
             */
            return grantToken(request, response, model);
        }

        /**
         * 需要用户授权
         */
        model.addAttribute("scopes", requestScopes);
        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("logoUrl", client.getLogoUrl());
        model.addAttribute("scopeNames", client.getScopes());
        model.addAttribute("clientName", client.getClientName());

        return "approval_scope";
    }
}
