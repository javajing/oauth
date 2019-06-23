package net.aulang.lang.oauth.server.qq;

import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.manage.ThirdPartyBiz;
import net.aulang.lang.oauth.server.core.Api;
import net.aulang.lang.oauth.server.impl.AbstractAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QQAuthService extends AbstractAuthService {
    public static final String QQ = "QQ";

    private QQApi api = new QQApi();
    @Autowired
    private ThirdPartyBiz thirdPartyBiz;

    @Override
    public ThirdPartyBiz getThirdPartyBiz() {
        return thirdPartyBiz;
    }

    @Override
    public Api getApi() {
        return api;
    }

    @Override
    public boolean supports(OAuthServer server) {
        return QQ.equals(server.getName());
    }
}
