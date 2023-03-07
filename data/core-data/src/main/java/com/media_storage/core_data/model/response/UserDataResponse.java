package com.media_storage.core_data.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDataResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String photoUrl;
}
