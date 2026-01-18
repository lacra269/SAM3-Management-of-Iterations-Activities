package com.dam.pms.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("PMS - Home")
public class MainView extends VerticalLayout {

    public MainView() {
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        // Header
        H1 header = new H1("📊 Project Management System");
        header.getStyle().set("color", "#1976D2");

        Paragraph description = new Paragraph(
                "Sistem complet de management al proiectelor, echipelor, activităților și riscurilor"
        );
        description.getStyle()
                .set("text-align", "center")
                .set("color", "#666")
                .set("max-width", "600px");

        // Menu Layout
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setSpacing(true);
        menuLayout.setWidth("400px");
        menuLayout.setPadding(false);

        // Proiecte Button
        Button projectsBtn = createMenuButton(" Proiecte", "projects",
                "Gestionează proiectele și iterațiile");

        // Activități Button
        Button activitiesBtn = createMenuButton(" Activități", "activities",
                "Vizualizează și editează activitățile");

        // Subtasks Button
        Button subtasksBtn = createMenuButton(" Subtasks", "subtasks",
                "Dashboard pentru subtask-uri și progres");

        // Membrii Echipei Button
        Button teamBtn = createMenuButton(" Membrii Echipei", "team-members",
                "Administrează membrii echipei");

        // Riscuri Button
        Button risksBtn = createMenuButton(" Riscuri", "risks",
                "Monitorizează și gestionează riscurile");

        menuLayout.add(projectsBtn, activitiesBtn, subtasksBtn, teamBtn, risksBtn);

        // REST API Info
        Paragraph apiInfo = new Paragraph(
                " REST API disponibil la: http://localhost:8080/api"
        );
        apiInfo.getStyle()
                .set("text-align", "center")
                .set("color", "#888")
                .set("font-size", "14px")
                .set("margin-top", "30px");

        add(header, description, menuLayout, apiInfo);
    }

    /**
     * Helper method pentru a crea butoane de meniu uniforme
     */
    private Button createMenuButton(String text, String route, String tooltip) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        button.setWidthFull();
        button.getStyle()
                .set("text-align", "left")
                .set("padding", "20px")
                .set("font-size", "18px");

        button.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(route))
        );

        // Tooltip
        button.getElement().setAttribute("title", tooltip);

        return button;
    }
}