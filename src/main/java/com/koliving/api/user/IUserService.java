package com.koliving.api.user;


import com.koliving.api.user.application.dto.UserResponse;

import java.util.List;

public interface IUserService {

    User save(User user);

    void setPassword(User user, String password);

    boolean isEqualPassword(String rawPassword, String hashPassword);

    List<UserResponse> list();
}
