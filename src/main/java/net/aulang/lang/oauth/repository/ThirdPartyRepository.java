package net.aulang.lang.oauth.repository;

import net.aulang.lang.oauth.document.ThirdParty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyRepository extends MongoRepository<ThirdParty, String> {

    ThirdParty findByThirdTypeAndThirdId(String thirdType, String thirdId);

}
