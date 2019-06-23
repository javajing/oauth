package net.aulang.lang.oauth.server.qq;

import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.server.core.AccessToken;
import net.aulang.lang.oauth.server.impl.AbstractApi;

import java.util.Map;

public class QQApi extends AbstractApi<QQProfile> {
    public static final String GET_USER_INFO = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    public QQApi() {
        setProfileExtractor(new QQProfileExtractor());
    }

    @Override
    public void getDetail(OAuthServer server, AccessToken accessToken, QQProfile profile) {
        try {
            String url = String.format(GET_USER_INFO,
                    accessToken.getAccessToken(),
                    profile.getClient_id(),
                    profile.getOpenid());
            String json = restTemplate.getForEntity(url, String.class).getBody();
            Map<String, ?> map = MAPPER.readValue(json, Map.class);
            profile.setNickname(map.get("nickname").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
