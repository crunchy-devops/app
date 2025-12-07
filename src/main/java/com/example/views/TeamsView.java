package com.example.views;

import com.example.data.Team;
import com.example.data.TeamRepository;
import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.security.PermitAll;

@PageTitle("Teams")
@Route(value = "teams", layout = MainLayout.class)
@PermitAll
public class TeamsView extends VerticalLayout {

    private final TeamRepository teamRepository;
    private final Grid<Team> grid = new Grid<>(Team.class);
    private final TextField nameField = new TextField("Team Name");
    private final TextField descriptionField = new TextField("Description");
    private final DatePicker creationDateField = new DatePicker("Creation Date");

    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Button cancelButton = new Button("Cancel");

    @Autowired
    public TeamsView(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;

        FormLayout form = new FormLayout();
        form.add(nameField, descriptionField, creationDateField);

        HorizontalLayout buttonLayout = createButtons();
        
        grid.setColumns("name", "description", "creationDate");
        
        add(form, buttonLayout, grid);
        
        refreshGrid();
    }

    private HorizontalLayout createButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        saveButton.addClickListener(e -> saveTeam());
        clearButton.addClickListener(e -> clearForm());
        cancelButton.addClickListener(e -> clearForm());

        return new HorizontalLayout(saveButton, clearButton, cancelButton);
    }

    private void saveTeam() {
        Team team = new Team();
        team.setName(nameField.getValue());
        team.setDescription(descriptionField.getValue());
        team.setCreationDate(creationDateField.getValue());
        teamRepository.save(team);
        refreshGrid();
        clearForm();
    }

    private void refreshGrid() {
        grid.setItems(teamRepository.findAll());
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        creationDateField.clear();
    }
}