package net.aulang.lang.oauth.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document
public class OAuthState implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String authorizeId;
    private String oAuthServerId;

    private String accountId;

    @Indexed(expireAfterSeconds = 600)
    private Date createdDatetime = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorizeId() {
        return authorizeId;
    }

    public void setAuthorizeId(String authorizeId) {
        this.authorizeId = authorizeId;
    }

    public String getoAuthServerId() {
        return oAuthServerId;
    }

    public void setoAuthServerId(String oAuthServerId) {
        this.oAuthServerId = oAuthServerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }
}
