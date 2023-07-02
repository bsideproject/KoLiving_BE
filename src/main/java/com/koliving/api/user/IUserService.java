package com.koliving.api.user;


public interface IUserService {

    User save(String email);

    void setPassword(User user, String password);

    boolean isEqualPassword(String rawPassword, String hashPassword);
}
