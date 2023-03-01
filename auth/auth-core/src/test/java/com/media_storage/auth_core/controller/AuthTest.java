package com.media_storage.auth_core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.auth_core.JsonAssertUtil;
import com.media_storage.auth_core.config.TestPersistenceConfig;
import com.media_storage.auth_core.entity.TokenEntity;
import com.media_storage.auth_core.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static com.media_storage.shared_data.constant.ExceptionConstants.EMAIL_NOT_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.TOKEN_NOT_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class AuthTest {

    private static final String USER_DETAILS_URL = "/v1/users";
    private static final String TOKEN_URL = "/v1/users/{userId}/tokens";
    private static final String VALID_EMAIL = "boris.johnson@gmail.com";
    private static final String USER_EMAIL_PARAM = "email";
    private static final Long INVALID_USER_ID = 999L;
    private static final Long USER_ID = 2L;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("classpath:data/sql/user/user.sql")
    void shouldGetUserDetails() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(USER_DETAILS_URL)
                        .param(USER_EMAIL_PARAM, VALID_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/user/UserDetailsResponse.json"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"dave.johnson@gmail.com", "harvey.williams@gmail.com", "wade.smith@gmail.com"})
    @Sql("classpath:data/sql/user/user.sql")
    void shouldNotGetUserDetailsThenNotFound(String email) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USER_DETAILS_URL)
                        .param(USER_EMAIL_PARAM, email)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.detail",
                        Matchers.containsString(String.format(EMAIL_NOT_EXISTS, email))
                ));
    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/user/user.sql",
            "classpath:data/sql/token/token.sql"
    })
    void shouldAddToken() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath(
                "data/json/token/TokenValidRequest.json"
        );

        mockMvc.perform(MockMvcRequestBuilders.put(TOKEN_URL, USER_ID)
                        .content(requestAsString)
                        .param(USER_EMAIL_PARAM, VALID_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<TokenEntity> token = tokenRepository.findByUserId(USER_ID);

        Assertions.assertTrue(token.isPresent());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/token/TokenEntity.json"),
                objectMapper.writeValueAsString(token.get())
        );
    }

    @Test
    @Sql("classpath:data/sql/user/user.sql")
    void shouldNotAddTokenThenNotFound() throws Exception {
        String authTokenModelAsString = JsonAssertUtil.readJsonFromClassPath(
                "data/json/token/TokenValidRequest.json"
        );

        mockMvc.perform(MockMvcRequestBuilders.put(TOKEN_URL, INVALID_USER_ID)
                        .content(authTokenModelAsString)
                        .param(USER_EMAIL_PARAM, VALID_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.detail",
                        Matchers.containsString(String.format(USER_NOT_EXISTS, INVALID_USER_ID))
                ));

        Optional<TokenEntity> token = tokenRepository.findByUserId(INVALID_USER_ID);

        Assertions.assertTrue(token.isEmpty());

    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/user/user.sql",
            "classpath:data/sql/token/token.sql"
    })
    void shouldGetToken() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_URL, USER_ID)
                        .param(USER_EMAIL_PARAM, VALID_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/token/TokenValidResponse.json"
        );
    }

    @ParameterizedTest
    @MethodSource("getTokenParams")
    @Sql("classpath:data/sql/user/user.sql")
    void shouldNotGetTokenThenNotFound(Long userId, String message) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_URL, userId)
                        .param(USER_EMAIL_PARAM, VALID_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", Matchers.containsString(message)));
    }

    private static Stream<Arguments> getTokenParams() {
        return Stream.of(
                Arguments.of(USER_ID, String.format(TOKEN_NOT_EXISTS, USER_ID)),
                Arguments.of(INVALID_USER_ID, String.format(USER_NOT_EXISTS, INVALID_USER_ID))
        );
    }
}
