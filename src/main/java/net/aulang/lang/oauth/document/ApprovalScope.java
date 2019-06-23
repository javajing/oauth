package net.aulang.lang.oauth.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Document
@CompoundIndexes({
        @CompoundIndex(
                unique = true,
                name = "idx_approval_accountId_clientId",
                def = "{'accountId':1, 'clientId':1}"
        )
})
public class ApprovalScope implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String clientId;
    private String accountId;

    private Set<String> approved;
    private Set<String> denied;

    @Indexed(expireAfterSeconds = 0)
    private Date expiresAt;
    private Date lastUpdatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Set<String> getApproved() {
        return approved;
    }

    public void setApproved(Set<String> approved) {
        this.approved = approved;
    }

    public Set<String> getDenied() {
        return denied;
    }

    public void setDenied(Set<String> denied) {
        this.denied = denied;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
