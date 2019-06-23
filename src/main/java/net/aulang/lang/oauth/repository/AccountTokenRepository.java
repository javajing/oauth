package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.AccountToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTokenRepository extends MongoRepository<AccountToken, String> {

    List<AccountToken> findByClientId(String clientId);

    AccountToken findByAccessToken(String accessToken);

    AccountToken findByRefreshToken(String refreshToken);

    List<AccountToken> findByAccountIdAndClientId(String accountId, String clientId);

    AccountToken findByAccountIdAndClientIdAndRedirectUrl(String accountId, String clientId, String redirectUrl);

}
