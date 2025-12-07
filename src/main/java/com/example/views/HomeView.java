package com.example.views;

import com.example.views.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        getStyle().set("background-image", "url('themes/default/book_signing_queue.png')");
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center");
    }
}