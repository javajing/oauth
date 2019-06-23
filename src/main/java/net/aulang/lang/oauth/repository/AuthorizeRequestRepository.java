package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.AuthorizeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizeRequestRepository extends MongoRepository<AuthorizeRequest, String> {

}
