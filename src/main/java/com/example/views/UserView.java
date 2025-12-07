package com.example.views;

import com.example.data.User;
import com.example.data.UserRepository;
import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.security.PermitAll;

@PageTitle("User")
@Route(value = "user", layout = MainLayout.class)
@PermitAll
public class UserView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final PasswordField newPasswordField = new PasswordField("New Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    private final Button changePasswordButton = new Button("Change Password");

    @Autowired
    public UserView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        add(newPasswordField, confirmPasswordField, changePasswordButton);

        changePasswordButton.addClickListener(e -> changePassword());
    }

    private void changePassword() {
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            Notification.show("Passwords do not match or are empty.", 3000, Notification.Position.MIDDLE);
            return;
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            Notification.show("Password changed successfully.", 3000, Notification.Position.MIDDLE);
            clearForm();
        }
    }

    private void clearForm() {
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}