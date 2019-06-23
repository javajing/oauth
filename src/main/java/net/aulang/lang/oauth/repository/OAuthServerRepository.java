package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.OAuthServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAuthServerRepository extends MongoRepository<OAuthServer, String> {
    List<OAuthServer> findByEnabledOrderBySortAsc(boolean enabled);

    OAuthServer findByNameAndEnabledIsTrue(String name);
}
