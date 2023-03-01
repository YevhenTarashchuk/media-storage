package com.media_storage.auth_data.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthTokenModel {
    private String accessToken;
    private String refreshToken;
}
