package com.media_storage.auth_core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.auth_core.config.TestPersistenceConfig;
import com.media_storage.auth_core.entity.UserEntity;
import com.media_storage.auth_core.repository.ConfirmationRepository;
import com.media_storage.auth_core.repository.UserRepository;
import com.media_storage.auth_data.enumeration.UserStatus;
import com.media_storage.auth_data.model.request.ConfirmationRequest;
import com.media_storage.shared.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static com.media_storage.auth_data.constant.AuthConstants.CONFIRMATION_EXPIRATION_TIME_IN_MIN;
import static com.media_storage.shared_data.constant.ExceptionConstants.CODE_IS_EXPIRED;
import static com.media_storage.shared_data.constant.ExceptionConstants.CONFIRMATION_NOT_EXISTS;
import static com.media_storage.shared_data.constant.ExceptionConstants.INVALID_CODE;
import static com.media_storage.shared_data.constant.ExceptionConstants.INVALID_USER_STATUS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class ConfirmationTest {

    private static final String EMAIL_CONFIRMATION_URL = "/v1/users/{userId}/confirmations";
    private static final String VALID_CODE = "0798";
    private static final Long USER_ID = 2L;

    @Autowired
    private ConfirmationRepository confirmationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {
            "classpath:data/sql/confirmation/user.sql",
            "classpath:data/sql/confirmation/confirmation.sql"
    })
    void shouldConfirmEmail() throws Exception {
        ConfirmationRequest request = new ConfirmationRequest(VALID_CODE);

        mockMvc.perform(MockMvcRequestBuilders.post(EMAIL_CONFIRMATION_URL, USER_ID)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> user = userRepository.findById(USER_ID);

        Assertions.assertTrue(confirmationRepository.findByUserId(USER_ID).isEmpty());
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(UserStatus.ACTIVE, user.get().getStatus());
    }

    @ParameterizedTest
    @MethodSource("getConfirmationParams")
    @Sql(scripts = {
            "classpath:data/sql/confirmation/user.sql",
            "classpath:data/sql/confirmation/confirmation.sql"
    })
    void shouldNotConfirmEmail(
            Long userId,
            String code,
            LocalDateTime current,
            ResultMatcher status,
            String message
    ) throws Exception {
        ConfirmationRequest request = new ConfirmationRequest(code);

        try (MockedStatic<LocalDateTimeUtil> utilities = Mockito.mockStatic(LocalDateTimeUtil.class)) {
            utilities.when(LocalDateTimeUtil::getInstantNow)
                    .thenReturn(current);
            mockMvc.perform(MockMvcRequestBuilders.post(EMAIL_CONFIRMATION_URL, userId)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status)
                    .andExpect(jsonPath("$.detail", Matchers.containsString(message)));
        }
    }

    private static Stream<Arguments> getConfirmationParams() {
        return Stream.of(
                Arguments.of(
                        1L,
                        VALID_CODE,
                        LocalDateTimeUtil.getInstantNow(),
                        status().isBadRequest(),
                        String.format(INVALID_USER_STATUS, 1L, UserStatus.EMAIL_CONFIRMATION)
                ),
                Arguments.of(
                        USER_ID,
                        INVALID_CODE,
                        LocalDateTimeUtil.getInstantNow(),
                        status().isBadRequest(),
                        INVALID_CODE
                ),
                Arguments.of(
                        USER_ID,
                        VALID_CODE,
                        LocalDateTimeUtil.getInstantNow().plusDays(CONFIRMATION_EXPIRATION_TIME_IN_MIN + 1),
                        status().isBadRequest(),
                        CODE_IS_EXPIRED
                ),
                Arguments.of(
                        3L,
                        VALID_CODE,
                        LocalDateTimeUtil.getInstantNow(),
                        status().isNotFound(),
                        String.format(CONFIRMATION_NOT_EXISTS, 3L)
                )
        );
    }
}
