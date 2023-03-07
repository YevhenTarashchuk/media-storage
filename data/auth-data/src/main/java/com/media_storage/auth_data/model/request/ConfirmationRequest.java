package com.media_storage.auth_data.model.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmationRequest(@NotBlank String code) { }
