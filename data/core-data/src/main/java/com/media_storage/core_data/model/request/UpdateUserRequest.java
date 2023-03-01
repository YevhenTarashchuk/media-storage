package com.media_storage.core_data.model.request;


import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String phone) {
}
