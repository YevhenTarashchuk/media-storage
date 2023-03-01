package com.media_storage.auth_data.constant;

public class AuthConstants {

    public static final String HEADER_PARAM_JWT_TOKEN = "Authorization";
    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String ROLE_ATTRIBUTE = "role";
    public static final int CONFIRMATION_EXPIRATION_TIME_IN_MIN = 15;
    public static final int CODE_RESEND_TIME_IN_MIN = 5;
    public static final String CODE_PARAM = "text";

    private AuthConstants() {
    }
}
