package com.media_storage.auth_core.service;

import com.media_storage.communication_client.CommunicationClient;
import com.media_storage.communication_data.enumeration.EmailType;
import com.media_storage.communication_data.model.EmailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.media_storage.auth_data.constant.AuthConstants.CODE_PARAM;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final CommunicationClient communicationClient;

    public void sendCodeEmail(String email, String code) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(Map.of(CODE_PARAM, code))
                .setEmailType(EmailType.CODE_CONFIRMATION)
                .setTo(Collections.singletonList(email));

        communicationClient.sendEvent(emailModel);
    }
}
