package com.media_storage.core_module.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.core_module.JsonAssertUtil;
import com.media_storage.core_module.config.TestPersistenceConfig;
import com.media_storage.core_module.entity.UserEntity;
import com.media_storage.core_module.repository.UserRepository;
import com.media_storage.media_client.MediaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static com.media_storage.shared_data.constant.ExceptionConstants.PHONE_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class UserTest {

    private static final String USER_ID_URL = "/v1/users/{userId}";
    private static final Long USER_ID = 1L;
    private static final Long INVALID_USER_ID = 2L;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MediaClient mediaClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUpdateUser() throws Exception {
        String requestPath = "data/json/user/UpdateUserRequest.json";
        MockMultipartFile photo = buildPhoto();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_ID_URL, USER_ID)
                        .file(buildRequest(requestPath))
                        .file(photo)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/user/UserDataResponse.json",
                "id", "photoUrl"
        );

        Optional<UserEntity> user = userRepository.findById(USER_ID);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertNotNull(user.get().getPhotoUrl());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/user/UserEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "photoUrl", "password", "createdAt", "updatedAt"
        );

        Mockito.verify(mediaClient).uploadFile(eq(photo), anyString());
    }

    @Test
    void shouldUpdateUserWithoutPhoto() throws Exception {
        String requestPath = "data/json/user/UpdateUserRequest.json";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_ID_URL, USER_ID)
                        .file(buildRequest(requestPath))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/user/UserDataResponse.json",
                "id", "photoUrl"
        );

        Optional<UserEntity> user = userRepository.findById(USER_ID);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertNull(user.get().getPhotoUrl());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/user/UserEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "photoUrl", "password", "createdAt", "updatedAt", "createdBy", "updatedBy"
        );

        Mockito.verifyNoInteractions(mediaClient);
    }

    @Test
    @Sql("classpath:data/sql/user/active_user.sql")
    void shouldNotUpdateUserThenBadRequest() throws Exception {
        String requestPath = "data/json/user/UpdateUserRequest.json";
        MockMultipartFile photo = buildPhoto();

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_ID_URL, INVALID_USER_ID)
                        .file(buildRequest(requestPath))
                        .file(photo)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(PHONE_EXISTS, "+380453345345"))));

        Mockito.verifyNoInteractions(mediaClient);
    }

    @Test
    @Sql("classpath:data/sql/user/active_user.sql")
    void shouldGetUser() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(USER_ID_URL, USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/user/UserDataResponse.json"
        );
    }

    @Test
    void shouldNotGetUserThenNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USER_ID_URL, USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(USER_NOT_EXISTS, USER_ID))));
    }

    @SneakyThrows
    private static MockMultipartFile buildPhoto() {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource("data/file/photo.jpeg").getInputStream());

        return new MockMultipartFile(
                "photo",
                "data/file/photo.jpeg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileForSavingInBytes
        );
    }

    private static MockMultipartFile buildRequest(String requestPath) throws IOException {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource(requestPath).getInputStream());

        return new MockMultipartFile(
                "request",
                "data/json/user/UpdateUserRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                fileForSavingInBytes
        );
    }
}
