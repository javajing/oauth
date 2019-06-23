package net.aulang.lang.oauth.controller;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.manage.AccountBiz;
import net.aulang.lang.oauth.manage.AuthorizeRequestBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AuthorizeRequestBiz authorizeRequestBiz;

    @PostMapping("/change_passwd")
    public String changePwd(@RequestParam(name = "authorize_id") String authorizeId,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "confirmed_password") String confirmedPassword,
                            Model model) {
        AuthorizeRequest request = authorizeRequestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (!password.equals(confirmedPassword)) {
            model.addAttribute("error", "两次密码不一致");
            return "change_passwd";
        }

        String accountId = request.getAccountId();
        if (!request.isAuthenticated() || accountId == null) {
            return Constants.errorPage(model, "用户未登录");
        }

        try {
            String result = accountBiz.changePassword(accountId, password, false);
            if (result == null) {
                return Constants.errorPage(model, "账号不存在");
            }
            request.setAccountId(null);
            request.setAuthenticated(false);
            authorizeRequestBiz.save(request);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.errorPage(model, e.getMessage());
        }

        model.addAttribute("authorizeId", authorizeId);
        return "change_success";
    }
}
