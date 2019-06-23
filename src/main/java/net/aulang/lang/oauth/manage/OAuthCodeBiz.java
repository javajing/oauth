package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.OAuthCode;
import net.aulang.lang.oauth.repository.OAuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class OAuthCodeBiz {
    @Autowired
    private OAuthCodeRepository dao;

    public OAuthCode save(OAuthCode code) {
        return dao.save(code);
    }

    public void delete(String id) {
        dao.deleteById(id);
    }

    public OAuthCode findOne(String id) {
        Optional<OAuthCode> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public OAuthCode consumeCode(String code) {
        OAuthCode authCode = findOne(code);
        if (authCode == null) {
            return null;
        }
        delete(authCode.getId());
        return authCode;
    }

    public OAuthCode create(String clientId, Set<String> scopes, String redirectUrl, String accountId) {
        OAuthCode code = new OAuthCode();
        code.setRedirectUrl(redirectUrl);
        code.setAccountId(accountId);
        code.setClientId(clientId);
        code.setScopes(scopes);
        return save(code);
    }
}
