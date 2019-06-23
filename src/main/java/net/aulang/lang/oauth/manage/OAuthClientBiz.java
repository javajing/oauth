package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.repository.OAuthClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthClientBiz {
    @Autowired
    private OAuthClientRepository dao;

    public OAuthClient findByClientId(String clientId) {
        return dao.findByClientId(clientId);
    }
}
