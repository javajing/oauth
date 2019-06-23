package net.aulang.lang.oauth.server.qq;

import net.aulang.lang.oauth.server.impl.AbstractProfile;

public class QQProfile extends AbstractProfile {
    private String client_id;
    private String openid;
    private String nickname;

    @Override
    public String getId() {
        return openid;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
