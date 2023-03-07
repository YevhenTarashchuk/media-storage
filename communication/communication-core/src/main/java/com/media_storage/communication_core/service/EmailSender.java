package com.media_storage.communication_core.service;


import com.media_storage.communication_data.model.EmailModel;

public interface EmailSender {

    void sendEmails(EmailModel request);
}
