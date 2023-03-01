package com.media_storage.communication_client;


import com.media_storage.communication_data.model.EmailModel;

public interface CommunicationClient {
    void sendEvent(EmailModel event);
}
