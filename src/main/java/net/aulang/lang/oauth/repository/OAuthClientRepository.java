package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.OAuthClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientRepository extends MongoRepository<OAuthClient, String> {

    OAuthClient findByClientId(String clientId);

}
