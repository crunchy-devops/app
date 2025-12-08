package com.example.views;

import com.example.data.User;
import com.example.data.UserRepository;
import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
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
    private User selectedUser;
    private boolean isCreateMode = false;

    private final TextField usernameField = new TextField("Username");
    private final TextField descriptionField = new TextField("Description");
    private final PasswordField newPasswordField = new PasswordField("New Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Button cancelButton = new Button("Cancel");
    private final Button createUserButton = new Button("Create User");
    private final Button backToEditButton = new Button("Back to Edit");

    public UserView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        FormLayout form = new FormLayout();
        form.add(usernameField, descriptionField, newPasswordField, confirmPasswordField);

        HorizontalLayout buttonLayout = createButtons();

        add(form, buttonLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadLoggedInUser();
    }

    private void loadLoggedInUser() {
        setCreateMode(false);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        selectedUser = userRepository.findByUsername(username);
        
        if (selectedUser != null) {
            usernameField.setValue(selectedUser.getUsername());
            descriptionField.setValue(selectedUser.getDescription() != null ? selectedUser.getDescription() : "");
        }
        
        clearPasswordFields();
    }

    private HorizontalLayout createButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        saveButton.addClickListener(e -> saveUser());
        clearButton.addClickListener(e -> clearForm());
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(HomeView.class));
        createUserButton.addClickListener(e -> enterCreateMode());
        backToEditButton.addClickListener(e -> loadLoggedInUser());

        return new HorizontalLayout(saveButton, clearButton, cancelButton, createUserButton, backToEditButton);
    }

    private void enterCreateMode() {
        setCreateMode(true);
        selectedUser = new User();
        clearForm();
    }

    private void setCreateMode(boolean createMode) {
        this.isCreateMode = createMode;
        usernameField.setReadOnly(!createMode);
        createUserButton.setVisible(!createMode);
        backToEditButton.setVisible(createMode);
    }

    private void saveUser() {
        if (isCreateMode) {
            if (usernameField.getValue().isEmpty()) {
                Notification.show("Username cannot be empty.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (userRepository.findByUsername(usernameField.getValue()) != null) {
                Notification.show("Username already exists.", 3000, Notification.Position.MIDDLE);
                return;
            }
        }

        selectedUser.setUsername(usernameField.getValue());
        selectedUser.setDescription(descriptionField.getValue());

        String newPassword = newPasswordField.getValue();
        if (!newPassword.isEmpty()) {
            if (newPassword.equals(confirmPasswordField.getValue())) {
                selectedUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                Notification.show("Passwords do not match.", 3000, Notification.Position.MIDDLE);
                return;
            }
        } else if (isCreateMode) {
            Notification.show("Password cannot be empty for a new user.", 3000, Notification.Position.MIDDLE);
            return;
        }

        userRepository.save(selectedUser);
        Notification.show("User saved successfully.", 3000, Notification.Position.MIDDLE);
        loadLoggedInUser();
    }

    private void clearForm() {
        if (isCreateMode) {
            usernameField.clear();
        }
        descriptionField.clear();
        clearPasswordFields();
    }

    private void clearPasswordFields() {
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}