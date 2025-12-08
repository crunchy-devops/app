package com.example.views.layout;

import com.example.SecurityService;
import com.example.views.CredentialView;
import com.example.views.HomeView;
import com.example.views.TeamsView;
import com.example.views.UserView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final Span userSpan = new Span();
    private final Span dateTime = new Span();

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("AnsibleFlow");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        Button logoutButton = new Button("Log out", e -> securityService.logout());

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, userSpan, dateTime, logoutButton);
        header.expand(logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // The user is guaranteed to be logged in when this is called.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        userSpan.setText("Welcome, " + username);
        dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                new RouterLink("Teams", TeamsView.class),
                new RouterLink("User", UserView.class),
                new RouterLink("Projects", HomeView.class),
                new RouterLink("Credentials", CredentialView.class),
                new RouterLink("Inventory", HomeView.class),
                new RouterLink("Logs", HomeView.class),
                new RouterLink("Settings", HomeView.class)
        ));
    }
}