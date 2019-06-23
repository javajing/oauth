package net.aulang.lang.oauth.server.core;

public interface ProfileExtractor {
    <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception;
}
