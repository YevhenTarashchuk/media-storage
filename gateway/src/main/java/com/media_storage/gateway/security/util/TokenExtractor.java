package com.media_storage.gateway.security.util;

public interface TokenExtractor {

    String extract(String payload);

}
