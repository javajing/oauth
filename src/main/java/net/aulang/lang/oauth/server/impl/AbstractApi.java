package net.aulang.lang.oauth.server.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.aulang.lang.oauth.common.Constants;
import net.aulang.lang.oauth.common.OAuthConstants;
import net.aulang.lang.oauth.document.OAuthServer;
import net.aulang.lang.oauth.factory.HttpConnectionFactory;
import net.aulang.lang.oauth.server.core.AccessToken;
import net.aulang.lang.oauth.server.core.Api;
import net.aulang.lang.oauth.server.core.ProfileExtractor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractApi<T extends AbstractProfile> implements Api<T> {
    protected static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final Class<T> entityClass;

    public AbstractApi() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(UTF_8);
            }
        }
    }

    protected ProfileExtractor profileExtractor = new DefaultProfileExtractor();
    protected RestTemplate restTemplate = new RestTemplate(HttpConnectionFactory.clientHttpRequestFactory());

    protected String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTIONMARK);
        params.forEach(
                (k, v) -> builder.append(k)
                        .append(Constants.EQUALMARK)
                        .append(v)
                        .append(Constants.ANDMARK)
        );
        return builder.toString();
    }

    protected Map<String, String> parseAccessToken(String accessToken) {
        Map<String, String> map = new HashMap<>();
        String[] params = accessToken.split(Constants.ANDMARK);
        for (String param : params) {
            String[] kv = param.split(Constants.EQUALMARK);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    protected HttpEntity<MultiValueMap<String, String>> buildPostParams(Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        params.forEach((k, v) -> map.add(k, v));
        return new HttpEntity(map, headers);
    }

    protected String getHttpResponse(String url,
                                     String method,
                                     Map<String, String> params,
                                     String accessToken,
                                     String authorization) {
        if (Constants.HEADER.equalsIgnoreCase(method)) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authorization + StringUtils.SPACE + accessToken);
            HttpEntity entity = new HttpEntity(headers);
            return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        } else if (Constants.POST.equalsIgnoreCase(method)) {
            HttpEntity<MultiValueMap<String, String>> requestEntity = buildPostParams(params);
            return restTemplate.postForEntity(url, requestEntity, String.class).getBody();
        } else {
            String getUrl = buildGetUrl(url, params);
            return restTemplate.getForEntity(getUrl, String.class).getBody();
        }
    }

    @Override
    public AccessToken getAccessToken(OAuthServer server, String code) throws IOException {
        String accessTokenUrl = server.getAccessTokenUrl();
        Map<String, String> accessTokenParams = server.getAccessTokenParams();
        accessTokenParams.put(OAuthConstants.CODE, code);

        String respone;
        if (HttpMethod.POST.matches(server.getAccessTokenMethod().toUpperCase())) {
            HttpEntity<MultiValueMap<String, String>> requestEntity = buildPostParams(accessTokenParams);
            respone = restTemplate.postForEntity(accessTokenUrl, requestEntity, String.class).getBody();
        } else {
            String url = buildGetUrl(accessTokenUrl, accessTokenParams);
            respone = restTemplate.getForEntity(url, String.class).getBody();
        }

        Map<String, String> responseMap;
        if (Constants.JSON.equalsIgnoreCase(server.getAccessTokenType())) {
            responseMap = MAPPER.readValue(respone, Map.class);
        } else {
            responseMap = parseAccessToken(respone);
        }

        AccessToken token = new AccessToken();

        Object expiresIn = responseMap.get(server.getExpiresInKey());
        token.setAccessToken(responseMap.get(server.getAccessTokenKey()));
        token.setRefreshToken(responseMap.get(server.getRefreshTokenKey()));
        token.setExpiresIn(expiresIn != null ? expiresIn.toString() : null);
        token.setAttributes(responseMap);

        return token;
    }

    @Override
    public T getProfile(OAuthServer server, AccessToken accessToken) throws Exception {
        String profileUrl = server.getProfileUrl();
        String profileMethod = server.getProfileMethod();
        Map<String, String> profileParams = server.getProfileParams();
        profileParams.put(server.getAccessTokenKey(), accessToken.getAccessToken());

        String responseBody = getHttpResponse(
                profileUrl,
                profileMethod,
                profileParams,
                accessToken.getAccessToken(),
                server.getProfileAuthorization());

        T profile = profileExtractor.extract(responseBody, entityClass);
        profile.setServerName(server.getName());
        return profile;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setProfileExtractor(ProfileExtractor profileExtractor) {
        this.profileExtractor = profileExtractor;
    }
}
