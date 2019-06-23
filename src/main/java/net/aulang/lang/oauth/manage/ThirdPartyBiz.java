package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.Account;
import net.aulang.lang.oauth.document.ThirdParty;
import net.aulang.lang.oauth.server.core.Profile;
import net.aulang.lang.oauth.repository.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyBiz {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private ThirdPartyRepository dao;

    public ThirdParty getAccount(Profile profile) {
        String profileId = profile.getId();
        String serverName = profile.getServerName();
        return dao.findByThirdTypeAndThirdId(serverName, profileId);
    }

    public Account register(Profile profile) {
        Account account = new Account();
        account.setNickname(profile.getUsername());
        account = accountBiz.register(account);

        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setThirdId(profile.getId());
        thirdParty.setThirdName(profile.getUsername());
        thirdParty.setAccountId(account.getId());
        thirdParty.setThirdType(profile.getServerName());
        dao.save(thirdParty);

        return account;
    }

    public ThirdParty bind(Account account, Profile profile) {
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setThirdId(profile.getId());
        thirdParty.setAccountId(account.getId());
        thirdParty.setThirdType(profile.getServerName());
        return dao.save(thirdParty);
    }
}
