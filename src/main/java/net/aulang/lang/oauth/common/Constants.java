package net.aulang.lang.oauth.common;

import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String SSO_COOKIE_NAME = "SSO";

    public static final String QUESTIONMARK = "?";
    public static final String EQUALMARK = "=";
    public static final String ANDMARK = "&";

    public static final String JSON = "json";

    public static final String POST = "post";
    public static final String HEADER = "header";
    public static final String BEARER = "Bearer";

    public static final String DEFAULT_IMAGE_FORMAT = "png";

    public static final String BIND_STATE_AUTHORIZE_ID = "bind";

    public static Map<String, String> error(String msg) {
        Map<String, String> error = new HashMap<>();
        error.put("error", msg);
        return error;
    }

    public static String errorPage(Model model, String msg) {
        model.addAttribute("error", msg);
        return "error";
    }

    public static Cookie ssoCookie(String value) {
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie removeSsoCookie() {
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
