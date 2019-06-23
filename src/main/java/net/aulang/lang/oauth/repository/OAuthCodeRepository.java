package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.OAuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthCodeRepository extends MongoRepository<OAuthCode, String> {

}
