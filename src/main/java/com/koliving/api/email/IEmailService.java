package com.koliving.api.email;

import com.koliving.api.user.domain.User;

public interface IEmailService {

    void sendMailAuth(MailType type, String to, String url);

    void sendRoomContact(String to, String contactInfo, String message, User sender, String link);

    void sendRoomReport(String link, String reason, User reporter);
}
