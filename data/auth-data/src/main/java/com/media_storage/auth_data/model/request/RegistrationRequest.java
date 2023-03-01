package com.media_storage.auth_data.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(@Email String email, @NotBlank String password) { }
