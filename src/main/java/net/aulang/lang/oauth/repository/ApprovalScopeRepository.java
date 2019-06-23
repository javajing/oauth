package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.ApprovalScope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalScopeRepository extends MongoRepository<ApprovalScope, String> {
    ApprovalScope findByClientIdAndAccountId(String clientId, String accountId);
}
