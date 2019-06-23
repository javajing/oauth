package net.aulang.lang.oauth.manage;

import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.common.OAuthConstants;
import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.model.Server;
import net.aulang.lang.oauth.repository.OAuthServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class OAuthServerBiz {
    @Autowired
    private OAuthStateBiz stateBiz;
    @Autowired
    private OAuthServerRepository dao;

    public static List<Server> servers;

    private String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTIONMARK);
        params.entrySet().forEach(
                e -> builder.append(e.getKey())
                        .append(Constants.EQUALMARK)
                        .append(e.getValue())
                        .append(Constants.ANDMARK)
        );
        return builder.toString();
    }

    public String buildAuthorizeUrl(String authorizeId, OAuthServer server, String accountId) {
        Map<String, String> params = server.getAuthorizeParams();
        String url = buildGetUrl(server.getAuthorizeUrl(), params);

        StringBuilder authorizeUrl = new StringBuilder(url);

        String state = stateBiz.create(authorizeId, server.getId(), accountId).getId();

        authorizeUrl.append(OAuthConstants.STATE).append(Constants.EQUALMARK).append(state);
        return authorizeUrl.toString();
    }

    public OAuthServer findOne(String id) {
        Optional<OAuthServer> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public OAuthServer findByName(String name) {
        return dao.findByNameAndEnabledIsTrue(name);
    }

    public List<OAuthServer> findEnabled() {
        return dao.findByEnabledOrderBySortAsc(true);
    }

    public List<Server> getAllServers() {
        if (servers == null) {
            syncServers();
        }
        return servers;
    }

    @Scheduled(cron = "0 0 6-22/1 * * ?")
    private void syncServers() {
        if (servers == null) {
            servers = new CopyOnWriteArrayList<>();
        } else {
            servers.clear();
        }

        findEnabled().forEach(e -> servers.add(new Server(e.getId(), e.getName(), e.getLogoUrl())));
    }
}
