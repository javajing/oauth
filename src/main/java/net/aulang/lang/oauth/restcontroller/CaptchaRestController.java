package net.aulang.lang.oauth.restcontroller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.document.AuthorizeRequest;
import net.aulang.lang.oauth.manage.AccountBiz;
import net.aulang.lang.oauth.manage.AuthorizeRequestBiz;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@RestController
public class CaptchaRestController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private DefaultKaptcha kaptcha;
    @Autowired
    private AuthorizeRequestBiz authorizeRequestBiz;

    @GetMapping(path = "/api/captcha/{authorizeId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<StreamingResponseBody> captcha(@PathVariable String authorizeId) {
        AuthorizeRequest request = authorizeRequestBiz.findOne(authorizeId);
        if (request != null) {
            String captcha = kaptcha.createText();
            request.setCaptcha(captcha);
            authorizeRequestBiz.save(request);

            BufferedImage image = kaptcha.createImage(captcha);
            return ResponseEntity.ok(outputStream -> ImageIO.write(image, Constants.DEFAULT_IMAGE_FORMAT, outputStream));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/api/captcha/{authorizeId}/{code}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> captcha(@PathVariable String authorizeId, @PathVariable String code) {
        AuthorizeRequest request = authorizeRequestBiz.findOne(authorizeId);
        if (request != null && code.equals(request.getCaptcha())) {
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.badRequest().body("false");
    }

    @PostMapping(path = "/api/captcha", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> captcha(@RequestParam(name = "authorize_id", required = false) String authorizeId,
                                          @RequestParam(name = "client_id", required = false) String clientId,
                                          @RequestParam(name = "mobile") String mobile) {
        AuthorizeRequest request;
        if (StringUtils.isNotBlank(authorizeId)) {
            /**
             * Web端
             */
            request = authorizeRequestBiz.findOne(authorizeId);
            if (request == null) {
                return ResponseEntity.badRequest().body("登录请求不存在或已失效");
            }
        } else {
            /**
             * 移动端
             */
            if (StringUtils.isBlank(clientId)) {
                return ResponseEntity.badRequest().body("参数不合法");
            }

            request = new AuthorizeRequest();
            request.setRedirectUrl("captcha");
            request.setClientId(clientId);
        }

        String captcha = RandomStringUtils.randomNumeric(6);

        String accountId;
        try {
            accountId = accountBiz.sendCaptcha(mobile, captcha);
            if (accountId == null) {
                return ResponseEntity.badRequest().body("账号未注册");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("发送验证码失败");
        }

        request.setMobile(mobile);
        request.setCaptcha(captcha);
        request.setAccountId(accountId);
        request = authorizeRequestBiz.save(request);

        return ResponseEntity.ok(request.getId());
    }
}
