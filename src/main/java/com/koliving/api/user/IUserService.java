package com.koliving.api.user;


public interface IUserService {

    void saveTokenAndSendEmail(String mail);

    User signUp(String email);

    void setPassword(User user, String password);

}
