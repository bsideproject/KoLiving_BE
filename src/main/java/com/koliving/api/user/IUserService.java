package com.koliving.api.user;


public interface IUserService {

    User save(User user);

    void setPassword(User user, String password);

    boolean isEqualPassword(String rawPassword, String hashPassword);
}
