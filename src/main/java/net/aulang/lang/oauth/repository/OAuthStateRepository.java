package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.OAuthState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthStateRepository extends MongoRepository<OAuthState, String> {
}
