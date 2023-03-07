package com.media_storage.gateway.security.token;


import java.io.Serializable;

public record JwtToken(String token) implements Serializable {

}
