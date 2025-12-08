package com.example.views;

import com.example.service.CredentialService;
import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Credentials")
@Route(value = "credentials", layout = MainLayout.class)
@PermitAll
public class CredentialView extends VerticalLayout {

    private final TextField credentialNameField = new TextField("Credential name");
    private final TextArea descriptionField = new TextArea("Description");

    private final Select<String> typeSelect = new Select<>();

    private final PasswordField githubTokenField = new PasswordField("GitHub token");

    private final TextField sshUsernameField = new TextField("SSH username");
    private final PasswordField sshPasswordField = new PasswordField("SSH password");
    private final PasswordField sshPrivateKeyField = new PasswordField("Private SSH key");

    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");

    private final CredentialService credentialService;

    public CredentialView(CredentialService credentialService) {
        this.credentialService = credentialService;
        configureFields();
        configureButtons();

        FormLayout formLayout = new FormLayout();
        formLayout.add(credentialNameField, descriptionField, typeSelect,
                githubTokenField,
                sshUsernameField, sshPasswordField, sshPrivateKeyField);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, clearButton);

        add(formLayout, buttons);
    }

    private void configureFields() {
        descriptionField.setHeight("120px");

        typeSelect.setLabel("Credential type");
        typeSelect.setItems("GitHub token", "SSH");
        typeSelect.setPlaceholder("Select type");

        // Default: hide all conditional fields until type selected
        githubTokenField.setVisible(false);
        sshUsernameField.setVisible(false);
        sshPasswordField.setVisible(false);
        sshPrivateKeyField.setVisible(false);

        typeSelect.addValueChangeListener(e -> updateVisibleFields());
    }

    private void updateVisibleFields() {
        String type = typeSelect.getValue();

        boolean github = "GitHub token".equals(type);
        boolean ssh = "SSH".equals(type);

        githubTokenField.setVisible(github);

        sshUsernameField.setVisible(ssh);
        sshPasswordField.setVisible(ssh);
        sshPrivateKeyField.setVisible(ssh);
    }

    private void configureButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(e -> saveCredential());
        clearButton.addClickListener(e -> clearForm());
    }

    private void saveCredential() {
        if (credentialNameField.isEmpty()) {
            Notification.show("Credential name is required", 3000, Notification.Position.MIDDLE);
            return;
        }

        String ctype = typeSelect.getValue();
        if (ctype == null) {
            Notification.show("Please select a credential type", 3000, Notification.Position.MIDDLE);
            return;
        }

        if ("GitHub token".equals(ctype)) {
            if (githubTokenField.isEmpty()) {
                Notification.show("GitHub token is required", 3000, Notification.Position.MIDDLE);
                return;
            }
        } else if ("SSH".equals(ctype)) {
            boolean hasPassword = !sshPasswordField.isEmpty();
            boolean hasPrivateKey = !sshPrivateKeyField.isEmpty();

            if (!hasPassword && !hasPrivateKey) {
                Notification.show("For SSH, either password or private key is required", 4000, Notification.Position.MIDDLE);
                return;
            }
        }

        String type = typeSelect.getValue();

        String normalizedType;
        if ("GitHub token".equals(type)) {
            normalizedType = "GITHUB_TOKEN";
        } else if ("SSH".equals(type)) {
            normalizedType = "SSH";
        } else {
            Notification.show("Unknown credential type", 3000, Notification.Position.MIDDLE);
            return;
        }

        credentialService.createCredential(
                credentialNameField.getValue(),
                descriptionField.getValue(),
                normalizedType,
                githubTokenField.getValue(),
                sshUsernameField.getValue(),
                sshPasswordField.getValue(),
                sshPrivateKeyField.getValue()
        );

        Notification.show("Credential saved", 3000, Notification.Position.MIDDLE);
        clearForm();
    }

    private void clearForm() {
        credentialNameField.clear();
        descriptionField.clear();
        typeSelect.clear();
        githubTokenField.clear();
        sshUsernameField.clear();
        sshPasswordField.clear();
        sshPrivateKeyField.clear();

        updateVisibleFields();
    }
}
