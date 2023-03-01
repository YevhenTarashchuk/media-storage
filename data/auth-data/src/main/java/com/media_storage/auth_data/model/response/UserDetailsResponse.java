package com.media_storage.auth_data.model.response;

import com.media_storage.auth_data.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponse {
    private Long userId;
    private String password;
    private Role role;
}
