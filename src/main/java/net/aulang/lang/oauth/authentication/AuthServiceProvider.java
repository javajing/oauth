package net.aulang.lang.oauth.authentication;

import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.server.core.AuthService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceProvider implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Collection<AuthService> services;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Collection<AuthService> getServices() {
        if (services == null) {
            Map<String, AuthService> serviceMap
                    = applicationContext.getBeansOfType(AuthService.class);
            services = serviceMap.values();
        }
        return services;
    }

    public AuthService get(OAuthServer server) {
        Optional<AuthService> optional =
                getServices()
                        .parallelStream()
                        .filter(e -> e.supports(server))
                        .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }
}
