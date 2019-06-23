package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.document.Account;
import net.aulang.lang.oauth.exception.PasswordExpiredException;
import net.aulang.lang.oauth.model.Profile;
import net.aulang.lang.oauth.repository.AccountRepository;
import net.aulang.lang.oauth.service.SmsService;
import net.aulang.lang.oauth.util.SHAUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AccountBiz {
    public static final Pattern MOBILE = Pattern.compile("^1\\d{10}$");
    public static final Pattern EMAIL = Pattern.compile("^\\S+@\\S+\\.\\S+$");

    @Autowired
    private SmsService smsService;
    @Autowired
    private AccountRepository dao;

    public Account findByUsername(String username) {
        return dao.findByUsernameAndStatusNot(username, Account.DELETED);
    }

    public Account findByMobile(String mobile) {
        return dao.findByMobileAndStatusNot(mobile, Account.DELETED);
    }

    public Account findByEmail(String email) {
        return dao.findByEmailAndStatusNot(email, Account.DELETED);
    }

    public String login(String loginName, String password)
            throws PasswordExpiredException, AccountLockedException {
        Account account = null;
        if (MOBILE.matcher(loginName).matches()) {
            account = findByMobile(loginName);
        } else if (EMAIL.matcher(loginName).matches()) {
            account = findByEmail(loginName);
        }
        if (account == null) {
            account = findByUsername(loginName);
        }

        if (account == null
                || !SHAUtils.SHA256(password).equals(account.getPassword())) {
            return null;
        }

        if (Account.DISABLED == account.getStatus()) {
            throw new AccountLockedException("账号被锁定，请申诉解锁");
        }
        if (account.isMustChangePassword()) {
            String reason = account.getMustChangePasswordReason();
            if (StringUtils.isBlank(reason)) {
                reason = "请修改密码";
            }
            throw new PasswordExpiredException(reason, account.getId());
        }

        return account.getId();
    }

    public Account register(Account account) {
        String password = account.getPassword();
        if (password != null) {
            account.setPassword(SHAUtils.SHA256(password));
        }
        return dao.save(account);
    }

    public Profile getUser(String id) {
        Optional<Account> optional = dao.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Account account = optional.get();
        return new Profile(account.getId(), account.getNickname());
    }

    public String changePassword(String id, String password, boolean mustChangePassword) {
        Optional<Account> optional = dao.findById(id);
        if (optional.isPresent()) {
            Account account = optional.get();
            account.setPassword(SHAUtils.SHA256(password));
            account.setMustChangePassword(mustChangePassword);
            dao.save(account);
            return id;
        }
        return null;
    }

    public String sendCaptcha(String mobile, String captcha) throws RuntimeException {
        Account account = findByMobile(mobile);
        if (account != null) {
            String content = "您申请的验证码是：" + captcha;
            int result = smsService.send(mobile, content);
            if (result < 0) {
                throw new RuntimeException("发送验证码失败");
            }
            return account.getId();
        }
        return null;
    }
}
