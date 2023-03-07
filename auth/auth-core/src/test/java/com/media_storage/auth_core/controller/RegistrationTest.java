package com.media_storage.auth_core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.auth_core.JsonAssertUtil;
import com.media_storage.auth_core.config.TestPersistenceConfig;
import com.media_storage.auth_core.entity.UserEntity;
import com.media_storage.auth_core.repository.ConfirmationRepository;
import com.media_storage.auth_core.repository.UserRepository;
import com.media_storage.auth_data.model.response.RegistrationResponse;
import com.media_storage.communication_client.CommunicationClient;
import com.media_storage.communication_data.model.EmailModel;
import com.media_storage.shared.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.media_storage.shared_data.constant.ExceptionConstants.CODE_RESEND_NOT_AVAILABLE;
import static com.media_storage.shared_data.constant.ExceptionConstants.EMAIL_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class RegistrationTest {

    private static final String USER_REGISTRATION_URL = "/v1/users/registrations";
    private static final String CODE_RESEND_TIME = "2023-02-01T11:38:45.300";
    private static final String MOCK_TIME = "2023-02-01T11:33:45";

    @Autowired
    private ConfirmationRepository confirmationRepository;
    @Autowired
    private CommunicationClient communicationClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUser() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath(
                "data/json/registration/UserRegistrationValidRequest.json"
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(USER_REGISTRATION_URL)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/registration/RegistrationResponse.json",
                "id"
        );

        Long userId = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(),
                RegistrationResponse.class
        ).getId();

        Optional<UserEntity> user = userRepository.findById(userId);

        Assertions.assertTrue(confirmationRepository.findByUserId(userId).isPresent());
        Assertions.assertTrue(user.isPresent());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/registration/UserEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "password", "createdAt", "updatedAt"
        );

        Mockito.verify(communicationClient).sendEvent(any(EmailModel.class));
    }

    @Test
    @Sql("classpath:data/sql/registration/active_user.sql")
    void shouldNotRegisterUserThenBadRequestWithExistingEmail() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath(
                "data/json/registration/UserRegistrationValidRequest.json"
        );

        mockMvc.perform(MockMvcRequestBuilders.put(USER_REGISTRATION_URL)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(EMAIL_EXISTS, "user@gmail.com"))));

        Mockito.verifyNoInteractions(communicationClient);
    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/registration/email_confirmation_user.sql",
            "classpath:data/sql/registration/confirmation.sql"
    })
    void shouldNotRegisterUserThenBadRequestWitInvalidResendTime() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath(
                "data/json/registration/UserRegistrationValidRequest.json"
        );

        try (MockedStatic<LocalDateTimeUtil> utilities = Mockito.mockStatic(LocalDateTimeUtil.class)) {
            utilities.when(LocalDateTimeUtil::getInstantNow)
                    .thenReturn(LocalDateTime.parse(MOCK_TIME));
            mockMvc.perform(MockMvcRequestBuilders.put(USER_REGISTRATION_URL)
                            .content(requestAsString)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(
                            "$.detail",
                            Matchers.containsString(String.format(CODE_RESEND_NOT_AVAILABLE, CODE_RESEND_TIME))
                    ));
        }

        Mockito.verifyNoInteractions(communicationClient);
    }
}
