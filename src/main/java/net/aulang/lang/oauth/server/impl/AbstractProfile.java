package net.aulang.lang.oauth.server.impl;

import net.aulang.lang.oauth.server.core.Profile;

public abstract class AbstractProfile implements Profile {
    protected String serverName;

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
