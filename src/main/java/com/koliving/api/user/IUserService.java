package com.koliving.api.user;


public interface IUserService {

    User signUp(String email);

    void setPassword(User user, String password);

}
