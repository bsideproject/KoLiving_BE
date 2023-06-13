package com.koliving.api.email;

public interface IEmailService {

    void send(MailType type, String to, String url);
}
