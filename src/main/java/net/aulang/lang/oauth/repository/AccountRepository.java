package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Account findByUsernameAndStatusNot(String username, int status);

    Account findByMobileAndStatusNot(String mobile, int status);

    Account findByEmailAndStatusNot(String email, int status);
}
