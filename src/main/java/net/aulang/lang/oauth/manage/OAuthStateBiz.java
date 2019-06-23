package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.OAuthState;
import net.aulang.lang.oauth.repository.OAuthStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuthStateBiz {
    @Autowired
    private OAuthStateRepository dao;

    public OAuthState save(OAuthState entity) {
        return dao.save(entity);
    }

    public OAuthState create(String authorizeId, String serverId, String accountId) {
        OAuthState oAuthState = new OAuthState();
        oAuthState.setAuthorizeId(authorizeId);
        oAuthState.setoAuthServerId(serverId);
        oAuthState.setAccountId(accountId);
        return save(oAuthState);
    }

    public OAuthState findByState(String state) {
        Optional<OAuthState> optional = dao.findById(state);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }
}
