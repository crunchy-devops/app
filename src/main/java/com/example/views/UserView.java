package com.example.views;

import com.example.data.User;
import com.example.data.UserRepository;
import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("User")
@Route(value = "user", layout = MainLayout.class)
@PermitAll
public class UserView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private User currentUser;

    private final TextField usernameField = new TextField("Username");
    private final TextField descriptionField = new TextField("Description");
    private final PasswordField newPasswordField = new PasswordField("New Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Button cancelButton = new Button("Cancel");

    public UserView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        FormLayout form = new FormLayout();
        usernameField.setReadOnly(true);
        form.add(usernameField, descriptionField, newPasswordField, confirmPasswordField);

        HorizontalLayout buttonLayout = createButtons();

        add(form, buttonLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadUserData();
    }

    private void loadUserData() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        currentUser = userRepository.findByUsername(username);
        if (currentUser != null) {
            usernameField.setValue(currentUser.getUsername());
            // Handle case where description might be null
            descriptionField.setValue(currentUser.getDescription() != null ? currentUser.getDescription() : "");
        }
    }

    private HorizontalLayout createButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        saveButton.addClickListener(e -> saveUser());
        clearButton.addClickListener(e -> clearForm());
        cancelButton.addClickListener(e -> loadUserData());

        return new HorizontalLayout(saveButton, clearButton, cancelButton);
    }

    private void saveUser() {
        currentUser.setDescription(descriptionField.getValue());

        String newPassword = newPasswordField.getValue();
        if (!newPassword.isEmpty()) {
            if (newPassword.equals(confirmPasswordField.getValue())) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                Notification.show("Passwords do not match.", 3000, Notification.Position.MIDDLE);
                return;
            }
        }

        userRepository.save(currentUser);
        Notification.show("Profile updated successfully.", 3000, Notification.Position.MIDDLE);
        clearPasswordFields();
    }

    private void clearForm() {
        loadUserData();
        clearPasswordFields();
    }

    private void clearPasswordFields() {
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}