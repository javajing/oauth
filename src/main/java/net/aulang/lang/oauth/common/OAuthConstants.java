package net.aulang.lang.oauth.common;

public class OAuthConstants {
    public static final String CODE = "code";
    public static final String STATE = "state";

    public static final String EXPIRES_IN = "expires_in";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static class GrantType {
        public static final String CODE = "code";
        public static final String IMPLICIT = "token";

        public static final String PASSWORD = "password";
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String AUTHORIZATION_CODE = "authorization_code";

        public static String responseType(String responseType) {
            if (CODE.equalsIgnoreCase(responseType)) {
                return AUTHORIZATION_CODE;
            } else if (IMPLICIT.equalsIgnoreCase(responseType)) {
                return "implicit";
            } else {
                return null;
            }
        }
    }
}
