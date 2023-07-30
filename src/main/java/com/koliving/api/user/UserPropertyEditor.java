package com.koliving.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Scope("prototype")
@Component
@RequiredArgsConstructor
public class UserPropertyEditor extends PropertyEditorSupport {

    private final UserService userService;

    @Override
    public String getAsText() {
        User user = (User) getValue();
        return user.getEmail();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String email = text.trim();
        User user = (User) userService.loadUserByUsername(email);
        if (user == null) {
            throw new IllegalArgumentException("no email value");
        }
        setValue(user);
    }
}
