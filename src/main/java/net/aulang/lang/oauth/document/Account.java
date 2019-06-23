package net.aulang.lang.oauth.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    public static final int DELETED = -1;

    @Id
    private String id;
    private String nickname;

    @Indexed(unique = true, sparse = true)
    private String username;
    private String password;

    @Indexed(unique = true, sparse = true)
    private String mobile;
    @Indexed(unique = true, sparse = true)
    private String email;

    boolean mustChangePassword = false;
    private String mustChangePasswordReason = "密码已过期，请修改密码！";

    private int status = ENABLED;
    private Date createdDate = new Date();

    public Account() {
    }

    public Account(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public String getMustChangePasswordReason() {
        return mustChangePasswordReason;
    }

    public void setMustChangePasswordReason(String mustChangePasswordReason) {
        this.mustChangePasswordReason = mustChangePasswordReason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
