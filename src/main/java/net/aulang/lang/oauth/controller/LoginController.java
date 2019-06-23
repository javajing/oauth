package net.aulang.lang.oauth.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.exception.PasswordExpiredException;
import net.aulang.lang.oauth.manage.AccountBiz;
import net.aulang.lang.oauth.manage.AuthorizeRequestBiz;
import net.aulang.lang.oauth.manage.OAuthClientBiz;
import net.aulang.lang.oauth.manage.ReturnPageBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LoginController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private DefaultKaptcha kaptcha;
    @Autowired
    private OAuthClientBiz clientBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;
    @Autowired
    private AuthorizeRequestBiz requestBiz;

    @GetMapping("/login/{authorizeId}")
    public String login(@PathVariable String authorizeId, HttpServletResponse response, Model model) {
        AuthorizeRequest request = requestBiz.findOne(authorizeId);

        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (request.isAuthenticated()) {
            return returnPageBiz.approvalPage(request, response, model);
        } else {
            return returnPageBiz.loginPage(request, null, model);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "username") String username,
                        @RequestParam(name = "password") String password,
                        @RequestParam(name = "captcha", required = false) String captcha,
                        HttpServletResponse response, Model model) {
        AuthorizeRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        OAuthClient client = clientBiz.findByClientId(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的client_id");
        }

        if (request.getTriedTimes() > 2) {
            if (request.getCaptcha() != null && !request.getCaptcha().equals(captcha)) {
                model.addAttribute("error", "验证码错误");
                return returnPageBiz.loginPage(request, client, model);
            }
        }

        try {
            String accountId = accountBiz.login(username, password);
            if (accountId != null) {
                request.setAccountId(accountId);
                request.setAuthenticated(true);
                request = requestBiz.save(request);
                return returnPageBiz.approvalPage(request, response, model);
            } else {
                model.addAttribute("error", "账号或密码错误");
            }
        } catch (AccountLockedException e) {
            return "account_locked";
        } catch (PasswordExpiredException e) {
            request.setAuthenticated(true);
            request.setAccountId(e.getAccountId());
            requestBiz.save(request);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("authorizeId", authorizeId);
            return "change_passwd";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        int triedTimes = request.getTriedTimes() + 1;
        if (request.getTriedTimes() > 2) {
            request.setCaptcha(kaptcha.createText());
        }
        request.setTriedTimes(triedTimes);
        request = requestBiz.save(request);

        return returnPageBiz.loginPage(request, client, model);
    }

    @PostMapping("/login/captcha")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "mobile") String mobile,
                        @RequestParam(name = "captcha") String captcha,
                        HttpServletResponse response, Model model) {
        AuthorizeRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (request.getMobile() != null && !mobile.equals(request.getMobile())) {
            model.addAttribute("error", "手机号码不匹配");
            return returnPageBiz.loginPage(request, null, model);
        }

        if (request.getAccountId() == null && !captcha.equals(request.getCaptcha())) {
            model.addAttribute("error", "验证码错误");
            return returnPageBiz.loginPage(request, null, model);
        }
        request.setAuthenticated(true);
        request = requestBiz.save(request);

        return returnPageBiz.approvalPage(request, response, model);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "redirect_url", required = false) String redirectUrl,
                       HttpServletResponse response) throws IOException {
        response.addCookie(Constants.removeSsoCookie());
        response.sendRedirect(redirectUrl != null ? redirectUrl : "/");
    }
}