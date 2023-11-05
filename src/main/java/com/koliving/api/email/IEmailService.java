package com.koliving.api.email;

import com.koliving.api.user.domain.User;

public interface IEmailService {

    void send(MailType type, String to, String url);

    void sendRoomContact(String to, String contactInfo, String message, User sender, String link);
}
