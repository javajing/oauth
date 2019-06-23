package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.ApprovalScope;
import net.aulang.lang.oauth.document.OAuthClient;
import net.aulang.lang.oauth.repository.ApprovalScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

@Service
public class ApprovalScopeBiz {
    @Autowired
    private ApprovalScopeRepository dao;

    public ApprovalScope save(ApprovalScope approvalScope) {
        return dao.save(approvalScope);
    }

    public ApprovalScope save(OAuthClient client, String accountId, String[] scopes) {
        ApprovalScope approvalScope = new ApprovalScope();

        approvalScope.setClientId(client.getClientId());
        approvalScope.setAccountId(accountId);

        Calendar calendar = Calendar.getInstance();
        approvalScope.setLastUpdatedAt(calendar.getTime());

        calendar.add(Calendar.SECOND, client.getApprovalValiditySeconds());
        approvalScope.setExpiresAt(calendar.getTime());

        approvalScope.setApproved(new HashSet<>(Arrays.asList(scopes)));
        return save(approvalScope);
    }

    public ApprovalScope findByAccountIdAndClientId(String accountId, String clientId) {
        return dao.findByClientIdAndAccountId(clientId, accountId);
    }
}
