package net.aulang.lang.oauth.server.qq;

import net.aulang.lang.oauth.server.core.Profile;
import net.aulang.lang.oauth.server.impl.DefaultProfileExtractor;

/**
 * callback( {"client_id":"APPID","openid":"OPENID"} );
 */
public class QQProfileExtractor extends DefaultProfileExtractor {
    private String getJson(String callback) {
        return callback
                .replace("callback(", "")
                .replace(");", "")
                .trim();
    }

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        String json = getJson(responseBody);
        return MAPPER.readValue(json, type);
    }
}
